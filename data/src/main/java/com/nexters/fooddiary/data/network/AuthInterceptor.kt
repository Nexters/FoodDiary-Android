package com.nexters.fooddiary.data.network

import android.util.Log
import com.nexters.fooddiary.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore
) : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val LOGIN_PATH = "/auth/login"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.url.encodedPath == LOGIN_PATH) {
            return chain.proceed(originalRequest)
        }

        val token = tokenStore.getCachedToken()

        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
