package com.nexters.fooddiary.data.repository

import android.content.Context
import android.net.Uri
import com.nexters.fooddiary.data.local.upload.PhotoUploadDao
import com.nexters.fooddiary.data.local.upload.PhotoUploadEntity
import com.nexters.fooddiary.data.local.upload.UploadStatus
import com.nexters.fooddiary.data.remote.photo.PhotoApi
import com.nexters.fooddiary.core.common.network.defaultMessage
import com.nexters.fooddiary.core.common.resource.ResourceProvider
import com.nexters.fooddiary.data.network.toNetworkError
import com.nexters.fooddiary.data.remote.photo.model.response.BatchUploadResultItem
import com.nexters.fooddiary.domain.repository.PhotoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import androidx.core.net.toUri

internal class PhotoRepositoryImpl @Inject constructor(
    private val photoApi: PhotoApi,
    private val photoUploadDao: PhotoUploadDao,
    private val resourceProvider: ResourceProvider,
    @ApplicationContext private val context: Context
) : PhotoRepository {

    override suspend fun batchUpload(
        date: LocalDate,
        photoUriStrings: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val uploadDateStr = date.toIsoDateString()
        when (val partsResult = buildMultipartParts(photoUriStrings)) {
            is PartsResult.Failure -> return@withContext Result.failure(partsResult.error)
            is PartsResult.Success -> {
                try {
                    val response = photoApi.batchUpload(uploadDateStr.toDatePart(), partsResult.parts)
                    recordPendingUploads(response.results, uploadDateStr)
                    Result.success(Unit)
                } catch (e: Exception) {
                    recordUploadFailure(uploadDateStr, e)
                    Result.failure(e)
                }
            }
        }
    }

    private fun buildMultipartParts(photoUriStrings: List<String>): PartsResult {
        if (photoUriStrings.isEmpty()) {
            return PartsResult.Failure(IllegalArgumentException("No photos to upload"))
        }
        val resolver = context.contentResolver
        val parts = photoUriStrings.mapIndexed { index, uriString ->
            uriToMultipartPart(resolver, uriString.toUri(), index)
        }
        val validParts = parts.filterNotNull()
        if (validParts.size != photoUriStrings.size) {
            return PartsResult.Failure(IllegalArgumentException("Failed to read some image files"))
        }
        return PartsResult.Success(validParts)
    }

    private suspend fun recordPendingUploads(results: List<BatchUploadResultItem>, uploadDateStr: String) {
        val entities = results.map { item ->
            PhotoUploadEntity(
                photoId = item.photoId,
                diaryId = item.diaryId,
                imageUrl = item.imageUrl,
                timeType = item.timeType,
                uploadDate = uploadDateStr,
                status = UploadStatus.PENDING
            )
        }
        photoUploadDao.insertAll(entities)
    }

    private suspend fun recordUploadFailure(uploadDateStr: String, error: Exception) {
        val message = error.toNetworkError().defaultMessage(resourceProvider)
        photoUploadDao.insert(
            PhotoUploadEntity(
                uploadDate = uploadDateStr,
                status = UploadStatus.FAILURE,
                errorMessage = message
            )
        )
    }

    private fun uriToMultipartPart(
        resolver: android.content.ContentResolver,
        uri: Uri,
        index: Int
    ): MultipartBody.Part? = try {
        resolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            val contentType = resolver.getType(uri) ?: "image/jpeg"
            val body = bytes.toRequestBody(contentType.toMediaTypeOrNull(), 0, bytes.size)
            MultipartBody.Part.createFormData("photos", "photo_$index.jpg", body)
        }
    } catch (e: Exception) {
        null
    }

    private sealed class PartsResult {
        data class Success(val parts: List<MultipartBody.Part>) : PartsResult()
        data class Failure(val error: Exception) : PartsResult()
    }
}

private fun LocalDate.toIsoDateString(): String =
    format(DateTimeFormatter.ISO_LOCAL_DATE)

private fun String.toDatePart(): okhttp3.RequestBody =
    toRequestBody("text/plain".toMediaTypeOrNull())
