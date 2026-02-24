package com.nexters.fooddiary.data.network

import com.nexters.fooddiary.core.common.network.NetworkError
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Throwable을 공통 [NetworkError]로 변환.
 * Retrofit/OkHttp 예외를 한 곳에서 처리해 UI·로깅에서 일관된 에러 처리가 가능함.
 */
fun Throwable.toNetworkError(): NetworkError = when (this) {
    is HttpException -> NetworkError.Http(
        code = code(),
        message = message()
    )
    is SocketTimeoutException -> NetworkError.Timeout
    is UnknownHostException -> NetworkError.NoConnection
    is IOException -> NetworkError.NoConnection
    else -> NetworkError.Unknown(message = message)
}
