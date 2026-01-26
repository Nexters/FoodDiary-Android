package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.datasource.remote.AuthRemoteDataSource
import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import com.nexters.fooddiary.data.remote.auth.model.response.LoginResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepositoryImpl
    private val authRemoteDataSource: AuthRemoteDataSource = mockk()

    @Before
    fun setUp() {
        authRepository = AuthRepositoryImpl(authRemoteDataSource)
    }

    @Test
    fun `로그인_성공_시_AuthInfo를_반환한다`() = runTest {
        // Given
        val provider = "google"
        val idToken = "token_123"
        val expectedResponse = LoginResponse(
            userId = "user_1",
            accessToken = "access_token_1",
            isFirst = true
        )

        coEvery { authRemoteDataSource.login(any()) } returns expectedResponse

        // When
        val result = authRepository.login(provider, idToken)

        // Then
        assertTrue(result.isSuccess)
        val authInfo = result.getOrNull()
        assertEquals("user_1", authInfo?.userId)
        assertEquals("access_token_1", authInfo?.accessToken)
        assertEquals(true, authInfo?.isFirst)

        coVerify {
            authRemoteDataSource.login(
                LoginRequest(
                    provider = provider,
                    idToken = idToken
                )
            )
        }
    }

    @Test
    fun `로그인_실패_시_Failure를_반환한다`() = runTest {
        // Given
        val provider = "google"
        val idToken = "token_123"
        val exception = RuntimeException("Network Error")

        coEvery { authRemoteDataSource.login(any()) } throws exception

        // When
        val result = authRepository.login(provider, idToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
