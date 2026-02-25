package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiarySummaryByDateItemResponse(
    @SerialName("photos")
    val photos: List<String>,
)
