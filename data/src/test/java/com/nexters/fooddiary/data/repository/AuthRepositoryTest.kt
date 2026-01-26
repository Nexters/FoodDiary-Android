package com.nexters.fooddiary.data.repository

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.nexters.fooddiary.core.common.auth.GoogleSignInIntentProvider
import com.nexters.fooddiary.data.local.TokenStore
import com.nexters.fooddiary.data.mapper.UserMapper
import com.nexters.fooddiary.data.remote.auth.AuthApi
import com.nexters.fooddiary.data.remote.auth.model.response.LoginResponse
import com.nexters.fooddiary.data.security.EncryptionKeyManager
import com.nexters.fooddiary.domain.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepositoryImpl
    private val authApi: AuthApi = mockk()
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val tokenStore: TokenStore = mockk(relaxed = true)
    private val userMapper: UserMapper = mockk()
    private val encryptionKeyManager: EncryptionKeyManager = mockk(relaxed = true)
    private val googleSignInIntentProvider: GoogleSignInIntentProvider = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(GoogleAuthProvider::class)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        authRepository = AuthRepositoryImpl(
            authApi,
            firebaseAuth,
            tokenStore,
            userMapper,
            encryptionKeyManager,
            googleSignInIntentProvider,
            context
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(GoogleAuthProvider::class)
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `signInWithGoogle_성공_시_User를_반환하고_토큰을_저장한다`() = runTest {
        // Given
        val idToken = "test_id_token"
        val firebaseAccessToken = "firebase_access_token"
        val mockCredential = mockk<AuthCredential>()
        val mockAuthResult = mockk<AuthResult>()
        val mockFirebaseUser = mockk<FirebaseUser>()
        val mockGetTokenResult = mockk<GetTokenResult>()
        val mockAuthTask = mockk<Task<AuthResult>>()
        val mockTokenTask = mockk<Task<GetTokenResult>>()
        val loginResponse = LoginResponse("test_uid", "access_token", true)
        val expectedUser = User("test_uid", "test@example.com", "Test User", "http://photo.url", true)

        every { GoogleAuthProvider.getCredential(idToken, null) } returns mockCredential
        every { firebaseAuth.signInWithCredential(mockCredential) } returns mockAuthTask
        coEvery { mockAuthTask.await() } returns mockAuthResult
        every { mockAuthResult.user } returns mockFirebaseUser
        every { mockFirebaseUser.getIdToken(true) } returns mockTokenTask
        coEvery { mockTokenTask.await() } returns mockGetTokenResult
        every { mockGetTokenResult.token } returns firebaseAccessToken
        coEvery { tokenStore.saveToken(firebaseAccessToken) } returns Unit
        coEvery { authApi.login(any()) } returns loginResponse
        every { userMapper.toDomainUser(mockFirebaseUser, true) } returns expectedUser

        // When
        val result = authRepository.signInWithGoogle(idToken)

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(expectedUser, user)

        coVerify { tokenStore.saveToken(firebaseAccessToken) }
        coVerify { 
            authApi.login(match {
                it.provider == "google" && it.idToken == firebaseAccessToken 
            }) 
        }
        verify { userMapper.toDomainUser(mockFirebaseUser, true) }
    }

    @Test
    fun `signInWithGoogle_실패_시_Failure를_반환한다`() = runTest {
        // Given
        val idToken = "test_id_token"
        val mockCredential = mockk<AuthCredential>()
        val mockAuthTask = mockk<Task<AuthResult>>()
        val exception = RuntimeException("SignIn failed")

        every { GoogleAuthProvider.getCredential(idToken, null) } returns mockCredential
        every { firebaseAuth.signInWithCredential(mockCredential) } returns mockAuthTask
        coEvery { mockAuthTask.await() } throws exception

        // When
        val result = authRepository.signInWithGoogle(idToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
