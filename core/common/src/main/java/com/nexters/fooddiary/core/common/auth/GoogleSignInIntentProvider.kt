package com.nexters.fooddiary.core.common.auth

import android.content.Context
import android.content.Intent

interface GoogleSignInIntentProvider {
    fun getSignInIntent(context: Context, webClientId: String): Intent
    fun getSignInResultFromIntent(context: Context, data: Intent?): Result<GoogleSignInAccount>
    suspend fun signOut(context: Context, webClientId: String)
}

data class GoogleSignInAccount(
    val idToken: String,
    val email: String?,
    val displayName: String?
)

sealed class SignInError {
    abstract val message: String
    abstract val details: String?
    
    data class DeveloperError(
        override val message: String,
        override val details: String? = null
    ) : SignInError()
    
    data class Cancelled(
        override val message: String,
        override val details: String? = null
    ) : SignInError()
    
    data class NetworkError(
        override val message: String,
        override val details: String? = null
    ) : SignInError()
    
    data class InternalError(
        override val message: String,
        override val details: String? = null
    ) : SignInError()
    
    data class Unknown(
        override val message: String,
        override val details: String? = null
    ) : SignInError()
}

class SignInException(
    val signInError: SignInError,
    cause: Throwable? = null
) : Exception(signInError.message, cause) {
    val details: String? = signInError.details
}

fun Throwable.getSignInErrorMessage(): String? {
    return when (val signInError = (this as? SignInException)?.signInError) {
        is SignInError.DeveloperError -> {
            signInError.details ?: signInError.message
        }
        is SignInError.Cancelled -> {
            null
        }
        is SignInError.NetworkError,
        is SignInError.InternalError,
        is SignInError.Unknown -> {
            signInError.message
        }
        else -> {
            this.message
        }
    }
}

fun Context.getWebClientId(): String {
    val resourceId = resources.getIdentifier(
        "custom_web_client_id",
        "string",
        packageName
    )
    if (resourceId != 0) {
        val clientId = resources.getString(resourceId)
        if (clientId.isNotBlank()) {
            return clientId
        }
    }
    return ""
}
