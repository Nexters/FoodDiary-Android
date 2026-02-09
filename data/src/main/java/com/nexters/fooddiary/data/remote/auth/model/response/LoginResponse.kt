package com.nexters.fooddiary.data.remote.auth.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("id")
    val userId: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("is_first")
    val isFirst: Boolean
)
