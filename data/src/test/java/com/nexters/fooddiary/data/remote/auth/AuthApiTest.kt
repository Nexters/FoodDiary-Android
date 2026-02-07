package com.nexters.fooddiary.data.remote.auth

import com.nexters.fooddiary.data.mock.BaseMockServerTest
import com.nexters.fooddiary.data.mock.MockUrlConfig
import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AuthApiTest : BaseMockServerTest() {

    private lateinit var authApi: AuthApi

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
            
        authApi = retrofit.create(AuthApi::class.java)
    }

    @Test
    fun `로그인_요청이_성공해야_한다`() = runTest {
        // Given
        val request = LoginRequest(provider = "google", idToken = "token_123")
        
        // When
        val response = authApi.login(request)

        // Then (Response Verification)
        assertEquals("mock_jwt_access_token_example", response.accessToken)
        assertEquals(false, response.isFirst)

        // Then (Request Verification)
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals(MockUrlConfig.PATH_LOGIN, recordedRequest.path)
        val requestBody = recordedRequest.body.readUtf8()
        assertEquals("""{"provider":"google","id_token":"token_123"}""", requestBody)
    }
}
