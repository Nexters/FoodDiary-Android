package com.nexters.fooddiary.data.remote.auth.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyTokenResponse(
    @SerialName("message")
    val message: String,
)
