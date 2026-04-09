package com.nexters.fooddiary.data.network

import com.nexters.fooddiary.core.common.network.AppErrorEvent
import com.nexters.fooddiary.core.common.network.AppErrorNotifier
import com.nexters.fooddiary.core.common.network.NetworkError
import com.nexters.fooddiary.data.local.TokenStore
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.CancellationException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val errorNotifier: AppErrorNotifier,
) : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val LOGIN_PATH = "/auth/login"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithAuth = if (originalRequest.url.encodedPath == LOGIN_PATH) {
            originalRequest
        } else {
            val token = tokenStore.getCachedToken()
            if (token.isNullOrBlank()) {
                originalRequest
            } else {
                originalRequest.newBuilder()
                    .header(HEADER_AUTHORIZATION, "Bearer $token")
                    .build()
            }
        }

        return try {
            val response = chain.proceed(requestWithAuth)
            if (!response.isSuccessful) {
                errorNotifier.notify(
                    AppErrorEvent(
                        error = NetworkError.Http(
                            code = response.code,
                            message = response.extractServerErrorMessage(),
                        ),
                        path = originalRequest.url.encodedPath,
                    )
                )
            }
            response
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            errorNotifier.notify(
                AppErrorEvent(
                    error = throwable.toNetworkError(),
                    path = originalRequest.url.encodedPath,
                )
            )
            throw throwable
        }
    }
}

private fun Response.extractServerErrorMessage(): String? {
    val rawBody = runCatching { peekBody(MAX_ERROR_BODY_BYTES).string() }.getOrNull()
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    val parsed = runCatching {
        val bodyObject = Json.parseToJsonElement(rawBody).jsonObject
        val messageKeys = listOf("message", "error", "detail", "errorMessage")
        messageKeys.firstNotNullOfOrNull { key ->
            bodyObject[key]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
        }
    }.getOrNull()

    return parsed ?: rawBody.take(MAX_ERROR_MESSAGE_LENGTH)
}

private const val MAX_ERROR_BODY_BYTES = 64L * 1024L
private const val MAX_ERROR_MESSAGE_LENGTH = 300
