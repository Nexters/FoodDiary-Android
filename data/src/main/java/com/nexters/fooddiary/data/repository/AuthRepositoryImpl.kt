package com.nexters.fooddiary.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nexters.fooddiary.core.common.auth.GoogleSignInIntentProvider
import com.nexters.fooddiary.core.common.auth.getWebClientId
import com.nexters.fooddiary.data.firebase.LoginDeviceInfoProvider
import com.nexters.fooddiary.data.local.TokenStore
import com.nexters.fooddiary.data.mapper.UserMapper
import com.nexters.fooddiary.data.remote.auth.AuthApi
import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import com.nexters.fooddiary.data.remote.auth.model.request.UpdateDeviceRequest
import com.nexters.fooddiary.data.security.EncryptionKeyManager
import com.nexters.fooddiary.domain.model.DeleteAccountError
import com.nexters.fooddiary.domain.model.DeleteAccountException
import com.nexters.fooddiary.domain.exception.AuthException
import com.nexters.fooddiary.domain.model.User
import com.nexters.fooddiary.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val firebaseAuth: FirebaseAuth,
    private val tokenStore: TokenStore,
    private val userMapper: UserMapper,
    private val encryptionKeyManager: EncryptionKeyManager,
    private val loginDeviceInfoProvider: LoginDeviceInfoProvider,
    private val googleSignInIntentProvider: GoogleSignInIntentProvider,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Firebase User is null")

            val firebaseAuthToken = firebaseUser.getIdToken(true).await()?.token
                ?: throw Exception("Failed to get Firebase ID Token")

            val deviceInfo = loginDeviceInfoProvider.getLoginDeviceInfo()
            val loginResponse = authApi.login(
                LoginRequest(
                    appVersion = deviceInfo.appVersion,
                    deviceId = deviceInfo.deviceId,
                    deviceToken = deviceInfo.deviceToken,
                    idToken = firebaseAuthToken,
                    isActive = deviceInfo.isActive,
                    osVersion = deviceInfo.osVersion,
                    provider = "google"
                )
            )
            tokenStore.saveToken(loginResponse.accessToken)

            userMapper.toDomainUser(firebaseUser, loginResponse.isFirst)
        }
    }

    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.let { userMapper.toDomainUser(it) }
    }

    override suspend fun verifyToken(): Result<Unit> {
        return try {
            authApi.verifyToken()
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(AuthException.InvalidToken())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncDeviceToken(deviceToken: String): Result<Unit> {
        return runCatching {
            if (deviceToken.isBlank()) return@runCatching

            tokenStore.initializeCache()
            if (tokenStore.getCachedToken().isNullOrBlank()) return@runCatching

            val deviceInfo = loginDeviceInfoProvider.getLoginDeviceInfo()
            authApi.updateMyDevice(
                UpdateDeviceRequest(
                    appVersion = deviceInfo.appVersion,
                    deviceId = deviceInfo.deviceId,
                    deviceToken = deviceToken,
                    isActive = deviceInfo.isActive,
                    osVersion = deviceInfo.osVersion
                )
            )
        }
    }

    override suspend fun initializeTokenCache() {
        tokenStore.initializeCache()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        kotlin.runCatching {
            tokenStore.deleteToken()
            tokenStore.deleteNickname()
        }
        val webClientId = context.getWebClientId()
        if (webClientId.isNotEmpty()) {
            kotlin.runCatching { googleSignInIntentProvider.signOut(context, webClientId) }
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(
                    DeleteAccountException(DeleteAccountError.NoUserSignedIn)
                )

            try {
                user.delete().await()
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                return Result.failure(
                    DeleteAccountException(DeleteAccountError.RecentLoginRequired)
                )
            } catch (e: Exception) {
                return Result.failure(
                    DeleteAccountException(DeleteAccountError.Unknown(e))
                )
            }

            kotlin.runCatching {
                tokenStore.deleteToken()
                tokenStore.deleteNickname()
            }
            // 회원 탈퇴 시 서버 계정 삭제 API도 함께 호출한다.
            kotlin.runCatching { authApi.deleteMe() }
            kotlin.runCatching { firebaseAuth.signOut() }
            kotlin.runCatching { tokenStore.deleteToken() }
            kotlin.runCatching { encryptionKeyManager.deleteKey() }

            val webClientId = context.getWebClientId()
            if (webClientId.isNotEmpty()) {
                kotlin.runCatching { googleSignInIntentProvider.signOut(context, webClientId) }
                kotlin.runCatching { googleSignInIntentProvider.revokeAccess(context, webClientId) }
            }

            Result.success(Unit)
        } catch (e: DeleteAccountException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(
                DeleteAccountException(DeleteAccountError.Unknown(e))
            )
        }
    }
}
