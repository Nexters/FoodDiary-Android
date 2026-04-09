package com.nexters.fooddiary.data.network

import com.nexters.fooddiary.core.common.network.AppErrorEvent
import com.nexters.fooddiary.core.common.network.AppErrorNotifier
import com.nexters.fooddiary.core.common.network.NetworkError
import com.nexters.fooddiary.data.local.TokenStore
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
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
        val path = originalRequest.url.encodedPath

        if (path == LOGIN_PATH) {
            return proceedWithNotify(chain, originalRequest, path)
        }

        val token = tokenStore.getCachedToken()

        if (token.isNullOrBlank()) {
            return proceedWithNotify(chain, originalRequest, path)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "Bearer $token")
            .build()

        return proceedWithNotify(chain, authenticatedRequest, path)
    }

    private fun proceedWithNotify(
        chain: Interceptor.Chain,
        request: okhttp3.Request,
        path: String?,
    ): Response {
        return try {
            val response = chain.proceed(request)
            if (!response.isSuccessful) {
                errorNotifier.notify(
                    AppErrorEvent(
                        error = NetworkError.Http(
                            code = response.code,
                            message = response.extractServerErrorMessage(),
                        ),
                        path = path,
                    )
                )
            }
            response
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            errorNotifier.notify(
                AppErrorEvent(
                    error = throwable.toNetworkError(),
                    path = path,
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
        bodyObject.extractOpenApiDetailMessage()
            ?: bodyObject.extractOpenApiMessage()
    }.getOrNull()

    return parsed ?: rawBody.take(MAX_ERROR_MESSAGE_LENGTH)
}

private fun JsonObject.extractOpenApiDetailMessage(): String? =
    this["detail"]?.asOpenApiDetailMessage()

private fun JsonObject.extractOpenApiMessage(): String? =
    this["message"].asNonBlankString()

private fun JsonElement.asOpenApiDetailMessage(): String? = when (this) {
    is JsonPrimitive -> contentOrNull?.takeIf { it.isNotBlank() }
    is JsonObject -> this["msg"].asNonBlankString()
    is JsonArray -> firstNotNullOfOrNull { it.asOpenApiDetailMessage() }
}

private fun JsonElement?.asNonBlankString(): String? =
    (this as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }

private const val MAX_ERROR_BODY_BYTES = 64L * 1024L
private const val MAX_ERROR_MESSAGE_LENGTH = 300
