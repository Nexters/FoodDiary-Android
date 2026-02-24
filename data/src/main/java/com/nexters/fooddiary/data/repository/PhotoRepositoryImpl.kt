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
import com.nexters.fooddiary.data.remote.photo.model.response.BatchUploadDiaryItem
import com.nexters.fooddiary.data.mapper.toUploadStatus
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
import com.nexters.fooddiary.data.firebase.LoginDeviceInfoProvider
import javax.inject.Named

internal class PhotoRepositoryImpl @Inject constructor(
    private val photoApi: PhotoApi,
    private val photoUploadDao: PhotoUploadDao,
    private val resourceProvider: ResourceProvider,
    private val loginDeviceInfoProvider: LoginDeviceInfoProvider,
    @Named("isDebug") private val isDebug: Boolean,
    @ApplicationContext private val context: Context,
) : PhotoRepository {

    override suspend fun clearPendingUploads() {
        withContext(Dispatchers.IO) {
            photoUploadDao.deleteAllPending()
        }
    }

    override suspend fun batchUpload(
        date: LocalDate,
        photoUriStrings: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val uploadDateStr = date.toIsoDateString()
        when (val partsResult = buildMultipartParts(photoUriStrings)) {
            is PartsResult.Failure -> return@withContext Result.failure(partsResult.error)
            is PartsResult.Success -> return@withContext uploadAndRecord(
                partsResult.parts,
                uploadDateStr
            )
        }
    }

    private suspend fun uploadAndRecord(
        parts: List<MultipartBody.Part>,
        uploadDateStr: String
    ): Result<Unit> {
        return try {
            val response = photoApi.batchUpload(
                testMode = isDebug,
                date = uploadDateStr.toDatePart(),
                photos = parts,
                deviceId = loginDeviceInfoProvider.getLoginDeviceInfo().deviceId.toRequestBody(
                    MEDIA_TYPE_TEXT_PLAIN.toMediaTypeOrNull()
                ),
            )
            recordPendingUploads(response.diaries, response.diaryDate)
            Result.success(Unit)
        } catch (e: Exception) {
            recordUploadFailure(uploadDateStr, e)
            Result.failure(e)
        }
    }

    private fun buildMultipartParts(photoUriStrings: List<String>): PartsResult {
        if (photoUriStrings.isEmpty()) {
            return PartsResult.Failure(IllegalArgumentException(ERROR_NO_PHOTOS))
        }
        val resolver = context.contentResolver
        val parts = photoUriStrings.mapIndexed { index, uriString ->
            uriToMultipartPart(resolver, uriString.toUri(), index)
        }
        val failedUris = parts.mapIndexed { index, part ->
            if (part == null) photoUriStrings[index] else null
        }.filterNotNull()
        if (failedUris.isNotEmpty()) {
            return PartsResult.Failure(
                IllegalArgumentException("$ERROR_READ_IMAGE_FAILED: ${failedUris.joinToString()}")
            )
        }
        return PartsResult.Success(parts.filterNotNull())
    }

    private suspend fun recordPendingUploads(
        diaries: List<BatchUploadDiaryItem>,
        uploadDateStr: String
    ) {
        val entities = diaries.mapNotNull { item ->
            val status = item.toUploadStatus() ?: return@mapNotNull null
            PhotoUploadEntity(
                photoId = null,
                diaryId = item.diaryId,
                imageUrl = null,
                timeType = null,
                uploadDate = uploadDateStr,
                status = status
            )
        }
        if (entities.isNotEmpty()) {
            photoUploadDao.insertAll(entities)
        }
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
            val contentType = resolver.getType(uri) ?: MIME_TYPE_IMAGE_JPEG
            val body = bytes.toRequestBody(contentType.toMediaTypeOrNull(), 0, bytes.size)
            MultipartBody.Part.createFormData(MULTIPART_FIELD_PHOTOS, "photo_$index.jpg", body)
        }
    } catch (e: Exception) {
        null
    }

    private sealed class PartsResult {
        data class Success(val parts: List<MultipartBody.Part>) : PartsResult()
        data class Failure(val error: Exception) : PartsResult()
    }
}

private const val MEDIA_TYPE_TEXT_PLAIN = "text/plain"
private const val MIME_TYPE_IMAGE_JPEG = "image/jpeg"
private const val MULTIPART_FIELD_PHOTOS = "photos"
private const val ERROR_NO_PHOTOS = "No photos to upload"
private const val ERROR_READ_IMAGE_FAILED = "Failed to read image files"

private fun LocalDate.toIsoDateString(): String =
    format(DateTimeFormatter.ISO_LOCAL_DATE)

private fun String.toDatePart(): okhttp3.RequestBody =
    toRequestBody("text/plain".toMediaTypeOrNull())
