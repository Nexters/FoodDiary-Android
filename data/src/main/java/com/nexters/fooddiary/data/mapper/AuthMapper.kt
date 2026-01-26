package com.nexters.fooddiary.data.mapper

import com.nexters.fooddiary.data.remote.auth.model.response.LoginResponse
import com.nexters.fooddiary.domain.model.AuthInfo

internal fun LoginResponse.toDomain(): AuthInfo {
    return AuthInfo(
        userId = userId,
        accessToken = accessToken,
        isFirst = isFirst
    )
}
