package com.nexters.fooddiary.data.remote.diary

import com.nexters.fooddiary.data.mock.BaseMockServerTest
import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryRequest
import com.nexters.fooddiary.data.remote.diary.model.UpdateDiaryRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class DiaryApiTest : BaseMockServerTest() {

    private lateinit var diaryApi: DiaryApi

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
            
        diaryApi = retrofit.create(DiaryApi::class.java)
    }

    @Test
    fun `특정_날짜의_다이어리_조회시_올바른_데이터를_반환해야_한다`() = runTest {
        // Given
        val date = "2026-01-17"
        
        // When
        val response = diaryApi.getDiary(
            startDate = date,
            endDate = date,
        )

        // Then (Response Verification)
        assertEquals(2, response.diaries.size)
        assertEquals("명동교자", response.diaries[0].restaurantName)
        assertEquals(42L, response.diaries[0].diaryId)

        // Then (Request Verification)
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals(
            "/diaries?start_date=2026-01-17&end_date=2026-01-17&test_mode=true",
            recordedRequest.path,
        )
    }
    
    @Test
    fun `다이어리_생성_요청을_검증한다`() = runTest {
        // Given
        val request = CreateDiaryRequest(date = "2026-01-18", note = "New Diary")
        
        // When
        val response = diaryApi.createDiary(request)
        
        // Then
        assertEquals(1L, response.diaryId)
        
        // Verify Request
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        val body = recordedRequest.body.readUtf8()
        assertNotNull(body)
    }

    @Test
    fun `특정_주간_다이어리_요약을_조회할_수_있다`() = runTest {
        // Given
        val startDate = "2026-02-22"
        val endDate = "2026-02-28"

        // When
        val response = diaryApi.getDiarySummary(
            startDate = startDate,
            endDate = endDate,
        )

        // Then (Response Verification)
        val firstDay = response["2026-02-22"]
        assertNotNull(firstDay)
        assertEquals(2, firstDay?.photos?.size)
        assertEquals("https://picsum.photos/seed/20260222a/400/300", firstDay?.photos?.first()?.url)

        // Then (Request Verification)
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals(
            "/diaries/summary?start_date=2026-02-22&end_date=2026-02-28&test_mode=true",
            recordedRequest.path,
        )
    }

    @Test
    fun `다이어리_수정시_address_name을_요청_바디에_포함한다`() = runTest {
        // Given
        val fallbackDispatcher = mockWebServer.dispatcher
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return if (request.method == "PATCH" && request.path == "/diaries/42") {
                    MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(
                            """
                            {
                              "id": 42,
                              "diary_date": "2026-03-12T15:09:52.066Z",
                              "time_type": "lunch",
                              "analysis_status": "done",
                              "restaurant_name": "식당",
                              "restaurant_url": "https://example.com",
                              "address_name": "서울시 강남구 테헤란로 123",
                              "road_address": "서울시 강남구 테헤란로 123",
                              "category": "한식",
                              "note": "노트",
                              "tags": ["점심"],
                              "cover_photo_url": "https://example.com/cover.jpg",
                              "user_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                              "cover_photo_id": 100,
                              "created_at": "2026-03-12T15:09:52.066Z",
                              "updated_at": "2026-03-12T15:09:52.066Z",
                              "photo_count": 1,
                              "photos": [
                                { "photo_id": 100, "image_url": "https://example.com/cover.jpg" }
                              ]
                            }
                            """.trimIndent()
                        )
                } else {
                    fallbackDispatcher.dispatch(request)
                }
            }
        }

        val request = UpdateDiaryRequest(
            category = "한식",
            restaurantName = "식당",
            restaurantUrl = "https://example.com",
            addressName = "서울시 강남구 테헤란로 123",
            roadAddress = "서울시 강남구 테헤란로 123",
            tags = listOf("점심"),
            note = "노트",
            coverPhotoId = 100,
            photoIds = listOf(100),
        )

        // When
        diaryApi.updateDiary(diaryId = 42, request = request)

        // Then
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("PATCH", recordedRequest.method)
        assertEquals("/diaries/42", recordedRequest.path)
        val body = recordedRequest.body.readUtf8()
        assertTrue(body.contains("\"address_name\":\"서울시 강남구 테헤란로 123\""))
    }
}
