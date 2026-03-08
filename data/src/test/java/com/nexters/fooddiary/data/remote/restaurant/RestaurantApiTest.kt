package com.nexters.fooddiary.data.remote.restaurant

import com.nexters.fooddiary.data.mock.BaseMockServerTest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RestaurantApiTest : BaseMockServerTest() {

    private lateinit var restaurantApi: RestaurantApi

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

        restaurantApi = retrofit.create(RestaurantApi::class.java)
    }

    @Test
    fun `식당_검색_요청시_목록_데이터를_반환해야_한다`() = runTest {
        // When
        val response = restaurantApi.searchRestaurant(
            diaryId = 12L,
            keyword = "김밥",
            page = 1,
            size = 15,
        )

        // Then
        assertEquals(3, response.restaurants.size)
        assertEquals("김밥천국", response.restaurants.first().name)
        assertEquals(3, response.totalCount)
        assertEquals(1, response.page)
        assertEquals(15, response.size)
        assertEquals(true, response.isEnd)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/restaurant/search", recordedRequest.requestUrl?.encodedPath)
        assertEquals("12", recordedRequest.requestUrl?.queryParameter("diary_id"))
        assertEquals("김밥", recordedRequest.requestUrl?.queryParameter("keyword"))
        assertEquals("1", recordedRequest.requestUrl?.queryParameter("page"))
        assertEquals("15", recordedRequest.requestUrl?.queryParameter("size"))
    }

    @Test
    fun `빈_검색어_요청시_추천_목록을_반환해야_한다`() = runTest {
        // When
        val response = restaurantApi.searchRestaurant(
            diaryId = 12L,
            keyword = null,
            page = 1,
            size = 15,
        )

        // Then
        assertEquals(3, response.restaurants.size)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("12", recordedRequest.requestUrl?.queryParameter("diary_id"))
        assertEquals(null, recordedRequest.requestUrl?.queryParameter("keyword"))
    }
}
