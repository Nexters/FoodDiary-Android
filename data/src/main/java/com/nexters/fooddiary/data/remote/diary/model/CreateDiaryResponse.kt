package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDiaryResponse(
    @SerialName("diary_id")
    val diaryId: Long
)