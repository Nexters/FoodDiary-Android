package com.nexters.fooddiary.core.common.network

import com.nexters.fooddiary.core.common.R
import com.nexters.fooddiary.core.common.resource.ResourceProvider

fun NetworkError.defaultMessage(resourceProvider: ResourceProvider): String = when (this) {
    is NetworkError.Http -> message ?: resourceProvider.getString(R.string.network_error_http, code)
    is NetworkError.Timeout -> resourceProvider.getString(R.string.network_error_timeout)
    is NetworkError.NoConnection -> resourceProvider.getString(R.string.network_error_no_connection)
    is NetworkError.Unknown -> message?.let { resourceProvider.getString(R.string.network_error_unknown_detail, it) }
        ?: resourceProvider.getString(R.string.network_error_unknown)
}
