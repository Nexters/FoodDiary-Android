package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiarySummaryPhoto(
    @SerialName("url")
    val url: String,
    @SerialName("diary_date")
    val diaryDate: String = "",
    @SerialName("road_address")
    val roadAddress: String? = null,
)

