package com.nexters.fooddiary.data.remote.photo

import com.nexters.fooddiary.data.mock.BaseMockServerTest
import com.nexters.fooddiary.data.remote.photo.model.request.ConfirmPhotoRequest
import com.nexters.fooddiary.data.remote.photo.model.request.GetUploadUrlRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class PhotoApiTest : BaseMockServerTest() {

    private lateinit var photoApi: PhotoApi

    @Before
    override fun setUp() {
        super.setUp()
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder().build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            
        photoApi = retrofit.create(PhotoApi::class.java)
    }

    @Test
    fun `업로드_URL_요청시_URL데이터를_반환해야_한다`() = runTest {
        // Given
        val request = GetUploadUrlRequest(
            diaryId = 1L,
            filename = "test.jpg",
            takenAt = "2026-01-17T12:00:00",
            latitude = 37.5665,
            longitude = 126.9780
        )
        
        // When
        val response = photoApi.getUploadUrl(request)

        // Then
        assertEquals(42L, response.photoId)
        assertEquals("https://s3.example.com/upload/signed_url_example", response.uploadUrl)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/photos", recordedRequest.path)
    }

    @Test
    fun `사진_분석_요청시_후보_데이터를_반환해야_한다`() = runTest {
        // Given
        val photoId = 42L
        
        // When
        val response = photoApi.analyzePhoto(photoId)

        // Then
        assertEquals(42L, response.photoId)
        assertEquals("한식", response.foodCategory)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/photos/42/analyze", recordedRequest.path)
    }

    @Test
    fun `사진_분석_결과_조회시_데이터를_반환해야_한다`() = runTest {
        // Given
        val photoId = 42L
        
        // When
        val response = photoApi.getAnalysisResult(photoId)

        // Then
        assertEquals(42L, response.photoId)
        assertEquals("명동교자", response.restaurantNameCandidates?.first()?.name)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/photos/42/analysis", recordedRequest.path)
    }

    @Test
    fun `사진_정보_확정_요청이_성공해야_한다`() = runTest {
        // Given
        val photoId = 42L
        val request = ConfirmPhotoRequest(
            restaurantName = "명동교자",
            menuName = "칼국수",
            menuPrice = 10000,
            timeType = "LUNCH"
        )
        
        // When
        val response = photoApi.confirmPhoto(photoId, request)

        // Then
        assertEquals(true, response.success)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/photos/42/confirm", recordedRequest.path)
        // Verify Body part
        val body = recordedRequest.body.readUtf8()
        assertEquals("""{"restaurant_name":"명동교자","menu_name":"칼국수","menu_price":10000,"time_type":"LUNCH"}""", body)
    }

    @Test
    fun `최종_기록_조회시_데이터를_반환해야_한다`() = runTest {
        // Given
        val photoId = 42L
        
        // When
        val response = photoApi.getFinalRecord(photoId)

        // Then
        assertEquals("명동교자", response.restaurantName)
        assertEquals(10000, response.menuPrice)
        assertEquals("lunch", response.timeType)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/photos/42/final", recordedRequest.path)
    }
}
