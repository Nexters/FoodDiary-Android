package com.nexters.fooddiary.core.common.network

import com.nexters.fooddiary.core.common.R
import com.nexters.fooddiary.core.common.resource.ResourceProvider

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

    fun defaultMessage(resourceProvider: ResourceProvider): String = when (this) {
        is Http -> message ?: resourceProvider.getString(R.string.network_error_http, code)
        is Timeout -> resourceProvider.getString(R.string.network_error_timeout)
        is NoConnection -> resourceProvider.getString(R.string.network_error_no_connection)
        is Unknown -> message?.let { resourceProvider.getString(R.string.network_error_unknown_detail, it) }
            ?: resourceProvider.getString(R.string.network_error_unknown)
    }
}
