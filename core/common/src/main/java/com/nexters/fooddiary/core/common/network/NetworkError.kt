package com.nexters.fooddiary.core.common.network

sealed class NetworkError {
    data class Http(
        val code: Int,
        val message: String?
    ) : NetworkError()

    data object Timeout : NetworkError()

    data object NoConnection : NetworkError()

    data class Unknown(
        val message: String?
    ) : NetworkError()
}
