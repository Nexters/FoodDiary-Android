package com.nexters.fooddiary.data.remote.auth.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("provider")
    val provider: String,
    @SerialName("id_token")
    val idToken: String
)