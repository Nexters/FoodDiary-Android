package com.nexters.fooddiary.data.repository

import android.content.Context
import android.net.Uri
import com.nexters.fooddiary.data.remote.photo.PhotoApi
import com.nexters.fooddiary.domain.repository.PhotoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class PhotoRepositoryImpl @Inject constructor(
    private val photoApi: PhotoApi,
    @ApplicationContext private val context: Context
) : PhotoRepository {

    override suspend fun batchUpload(
        date: LocalDate,
        photoUriStrings: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (photoUriStrings.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("No photos to upload"))
        }
        try {
            val datePart = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val resolver = context.contentResolver
            val photoParts = photoUriStrings.mapIndexed { index, uriString ->
                uriToPart(resolver, Uri.parse(uriString), index)
            }
            val validParts = photoParts.filterNotNull()
            if (validParts.size != photoUriStrings.size) {
                return@withContext Result.failure(IllegalArgumentException("Failed to read some image files"))
            }
            photoApi.batchUpload(datePart, validParts)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToPart(
        resolver: android.content.ContentResolver,
        uri: Uri,
        index: Int
    ): MultipartBody.Part? {
        return try {
            resolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val contentType = resolver.getType(uri) ?: "image/jpeg"
                val body = bytes.toRequestBody(contentType.toMediaTypeOrNull(), 0, bytes.size)
                val fileName = "photo_$index.jpg"
                MultipartBody.Part.createFormData("photos", fileName, body)
            }
        } catch (e: Exception) {
            null
        }
    }
}
