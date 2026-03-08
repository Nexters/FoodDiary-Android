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
        val request = LoginRequest(
            appVersion = "1.0.0",
            deviceId = "A1B2C3D4-E5F6-7890-ABCD-EF1234567890",
            deviceToken = "fcm_token_abc123",
            idToken = "token_123",
            isActive = true,
            osVersion = "18.2",
            provider = "google"
        )
        
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
        assertEquals(
            """{"app_version":"1.0.0","device_id":"A1B2C3D4-E5F6-7890-ABCD-EF1234567890","device_token":"fcm_token_abc123","id_token":"token_123","is_active":true,"os_version":"18.2","provider":"google"}""",
            requestBody
        )
    }
}
