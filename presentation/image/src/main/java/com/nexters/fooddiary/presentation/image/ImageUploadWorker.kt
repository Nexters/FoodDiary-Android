package com.nexters.fooddiary.presentation.image

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.nexters.fooddiary.domain.usecase.BatchUploadPhotosUseCase
import java.time.LocalDate
import java.time.format.DateTimeParseException

class ImageUploadWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val dateString = inputData.getString(KEY_DATE) ?: return Result.failure()
        val uriStrings = inputData.getStringArray(KEY_URIS)?.toList().orEmpty()
        if (uriStrings.isEmpty()) return Result.failure()

        val targetDate = try {
            LocalDate.parse(dateString)
        } catch (_: DateTimeParseException) {
            return Result.failure()
        }

        val batchUploadPhotosUseCase = EntryPointAccessors.fromApplication(
            applicationContext,
            UploadWorkerEntryPoint::class.java,
        ).batchUploadPhotosUseCase()

        return batchUploadPhotosUseCase(targetDate, uriStrings)
            .fold(
                onSuccess = { Result.success() },
                onFailure = { Result.retry() },
            )
    }

    companion object {
        private const val KEY_DATE = "key_date"
        private const val KEY_URIS = "key_uris"

        fun enqueue(
            context: Context,
            targetDate: LocalDate,
            uriStrings: List<String>,
        ) {
            val input = Data.Builder()
                .putString(KEY_DATE, targetDate.toString())
                .putStringArray(KEY_URIS, uriStrings.toTypedArray())
                .build()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<ImageUploadWorker>()
                .setInputData(input)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UploadWorkerEntryPoint {
    fun batchUploadPhotosUseCase(): BatchUploadPhotosUseCase
}
