package com.nexters.fooddiary.data.remote.photo.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUploadUrlRequest(
    @SerialName("diary_id")
    val diaryId: Long,
    @SerialName("filename")
    val filename: String,
    @SerialName("taken_at")
    val takenAt: String,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?
)
