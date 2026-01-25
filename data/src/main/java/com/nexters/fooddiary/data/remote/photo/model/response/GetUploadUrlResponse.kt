package com.nexters.fooddiary.data.remote.photo.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUploadUrlResponse(
    @SerialName("photo_id")
    val photoId: Long,
    @SerialName("upload_url")
    val uploadUrl: String
)
