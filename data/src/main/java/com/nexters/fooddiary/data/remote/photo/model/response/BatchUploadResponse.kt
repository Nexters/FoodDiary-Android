package com.nexters.fooddiary.data.remote.photo.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchUploadResponse(
    @SerialName("diary_date")
    val diaryDate: String,
    @SerialName("diaries")
    val diaries: List<BatchUploadDiaryItem>
)

@Serializable
data class BatchUploadDiaryItem(
    @SerialName("diary_id")
    val diaryId: Long,
    @SerialName("diary_status")
    val diaryStatus: String
)
