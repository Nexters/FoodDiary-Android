package com.nexters.fooddiary.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nexters.fooddiary.core.common.resource.ResourceProvider
import com.nexters.fooddiary.data.firebase.LoginDeviceInfo
import com.nexters.fooddiary.data.firebase.LoginDeviceInfoProvider
import com.nexters.fooddiary.data.local.upload.FoodDiaryDatabase
import com.nexters.fooddiary.data.local.upload.PhotoUploadDao
import com.nexters.fooddiary.data.local.upload.PhotoUploadEntity
import com.nexters.fooddiary.data.local.upload.UploadStatus
import com.nexters.fooddiary.data.remote.photo.PhotoApi
import com.nexters.fooddiary.data.remote.photo.model.request.ConfirmPhotoRequest
import com.nexters.fooddiary.data.remote.photo.model.request.GetUploadUrlRequest
import com.nexters.fooddiary.data.remote.photo.model.response.BatchUploadResponse
import com.nexters.fooddiary.data.remote.photo.model.response.ConfirmPhotoResponse
import com.nexters.fooddiary.data.remote.photo.model.response.GetUploadUrlResponse
import com.nexters.fooddiary.data.remote.photo.model.response.PhotoAnalysisResponse
import com.nexters.fooddiary.data.remote.photo.model.response.PhotoFinalRecordResponse
import com.nexters.fooddiary.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoRepositoryClearPendingUploadsTest {

    private lateinit var database: FoodDiaryDatabase
    private lateinit var photoUploadDao: PhotoUploadDao
    private lateinit var repository: PhotoRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FoodDiaryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        photoUploadDao = database.photoUploadDao()

        repository = PhotoRepositoryImpl(
            photoApi = FakePhotoApi(),
            photoUploadDao = photoUploadDao,
            resourceProvider = StubResourceProvider(),
            loginDeviceInfoProvider = FakeLoginDeviceInfoProvider(),
            isDebug = true,
            context = context,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun clearPendingUploads_deletesAllPendingRows() = runBlocking {
        // Given: one PENDING record in local Room
        photoUploadDao.insert(
            PhotoUploadEntity(
                uploadDate = "2026-02-24",
                status = UploadStatus.PENDING,
            )
        )

        val before = photoUploadDao.getAll().first()
        assertTrue(before.isNotEmpty())

        // When: clearPendingUploads is invoked (FCM 수신 시 호출되는 경로)
        repository.clearPendingUploads()

        // Then: all PENDING rows are removed
        val after = photoUploadDao.getAll().first()
        assertTrue(after.isEmpty())
    }
}

private class FakePhotoApi : PhotoApi {
    override suspend fun getUploadUrl(request: GetUploadUrlRequest): GetUploadUrlResponse {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun analyzePhoto(photoId: Long): PhotoAnalysisResponse {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun getAnalysisResult(photoId: Long): PhotoAnalysisResponse {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun confirmPhoto(
        photoId: Long,
        request: ConfirmPhotoRequest
    ): ConfirmPhotoResponse {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun getFinalRecord(photoId: Long): PhotoFinalRecordResponse {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun batchUpload(
        testMode: Boolean,
        date: RequestBody,
        deviceId: RequestBody,
        photos: List<MultipartBody.Part>
    ): BatchUploadResponse {
        throw UnsupportedOperationException("Not used in this test")
    }
}

private class StubResourceProvider : ResourceProvider {
    override fun getString(resId: Int): String = ""
    override fun getString(resId: Int, vararg formatArgs: Any): String = ""
    override fun getDimension(resId: Int): Float = 0f
}

private class FakeLoginDeviceInfoProvider : LoginDeviceInfoProvider {
    override suspend fun getLoginDeviceInfo(): LoginDeviceInfo {
        return LoginDeviceInfo(
            appVersion = "test",
            deviceId = "test-device-id",
            deviceToken = "test-token",
            isActive = true,
            osVersion = "test-os"
        )
    }
}

