package com.nexters.fooddiary.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nexters.fooddiary.core.common.auth.GoogleSignInIntentProvider
import com.nexters.fooddiary.core.common.auth.getWebClientId
import com.nexters.fooddiary.data.local.TokenStore
import com.nexters.fooddiary.data.mapper.UserMapper
import com.nexters.fooddiary.data.security.EncryptionKeyManager
import com.nexters.fooddiary.domain.model.User
import com.nexters.fooddiary.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val tokenStore: TokenStore,
    private val userMapper: UserMapper,
    private val encryptionKeyManager: EncryptionKeyManager,
    private val googleSignInIntentProvider: GoogleSignInIntentProvider,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let { firebaseUser ->
                kotlin.runCatching { tokenStore.saveToken(idToken) }
                Result.success(userMapper.toDomainUser(firebaseUser))
            } ?: Result.failure(Exception("User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.let { userMapper.toDomainUser(it) }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        kotlin.runCatching { tokenStore.deleteToken() }
        val webClientId = context.getWebClientId()
        if (webClientId.isNotEmpty()) {
            kotlin.runCatching { googleSignInIntentProvider.signOut(context, webClientId) }
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.delete().await()
                firebaseAuth.signOut()
                kotlin.runCatching { tokenStore.deleteToken() }
                kotlin.runCatching { encryptionKeyManager.deleteKey() }
                val webClientId = context.getWebClientId()
                if (webClientId.isNotEmpty()) {
                    kotlin.runCatching { googleSignInIntentProvider.signOut(context, webClientId) }
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user signed in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
