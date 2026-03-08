package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDiaryRequest(
    @SerialName("date")
    val date: String,
    @SerialName("note")
    val note: String
)
