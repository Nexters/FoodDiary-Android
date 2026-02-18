package com.nexters.fooddiary.data.remote.auth.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("app_version")
    val appVersion: String,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("device_token")
    val deviceToken: String,
    @SerialName("id_token")
    val idToken: String,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("os_version")
    val osVersion: String,
    @SerialName("provider")
    val provider: String
)
