@file:Suppress("DEPRECATION")

package com.nexters.fooddiary.data.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount as GmsGoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nexters.fooddiary.core.common.R
import com.nexters.fooddiary.core.common.auth.GoogleSignInAccount
import com.nexters.fooddiary.core.common.auth.GoogleSignInIntentProvider
import com.nexters.fooddiary.core.common.auth.SignInError
import com.nexters.fooddiary.core.common.auth.SignInException
import com.nexters.fooddiary.core.common.resource.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resourceProvider: ResourceProvider
) : GoogleSignInIntentProvider {
    private fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    override fun getSignInIntent(context: Context, webClientId: String): android.content.Intent {
        return getGoogleSignInClient(context, webClientId).signInIntent
    }

    override fun getSignInResultFromIntent(context: Context, data: android.content.Intent?): Result<GoogleSignInAccount> {
        data ?: return Result.failure(
            SignInException(SignInError.Unknown(resourceProvider.getString(R.string.sign_in_error_intent_null)))
        )
        
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val gmsAccount = task.getResult(ApiException::class.java)
            
            gmsAccount?.idToken?.let { idToken ->
                Result.success(
                    GoogleSignInAccount(
                        idToken = idToken,
                        email = gmsAccount.email,
                        displayName = gmsAccount.displayName
                    )
                )
            } ?: Result.failure(
                SignInException(SignInError.Unknown(resourceProvider.getString(R.string.sign_in_error_id_token_null)))
            )
        } catch (e: ApiException) {
            val error = mapToSignInError(e)
            Result.failure(SignInException(error, e))
        } catch (e: Exception) {
            val error = SignInError.Unknown(
                resourceProvider.getString(R.string.sign_in_error_unexpected, e.message ?: "")
            )
            Result.failure(SignInException(error, e))
        }
    }

    override suspend fun signOut(context: Context, webClientId: String) {
        getGoogleSignInClient(context, webClientId).signOut().await()
    }

    private fun mapToSignInError(exception: ApiException): SignInError {
        return when (exception.statusCode) {
            10 -> SignInError.DeveloperError(
                message = resourceProvider.getString(R.string.sign_in_error_developer),
                details = resourceProvider.getString(R.string.sign_in_error_developer_details)
            )
            12500 -> SignInError.Cancelled(
                resourceProvider.getString(R.string.sign_in_error_cancelled)
            )
            7 -> SignInError.NetworkError(
                resourceProvider.getString(R.string.sign_in_error_network)
            )
            8 -> SignInError.InternalError(
                resourceProvider.getString(R.string.sign_in_error_internal)
            )
            else -> SignInError.Unknown(
                resourceProvider.getString(R.string.sign_in_error_code, exception.statusCode, exception.message ?: "")
            )
        }
    }
}
