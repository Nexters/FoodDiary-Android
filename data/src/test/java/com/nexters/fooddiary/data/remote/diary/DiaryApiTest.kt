package com.nexters.fooddiary.data.remote.diary

import com.nexters.fooddiary.data.mock.BaseMockServerTest
import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
}
