package com.nexters.fooddiary.data.remote.photo.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPhotoResponse(
    @SerialName("success")
    val success: Boolean
)
