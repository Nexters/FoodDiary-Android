package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryPhoto(
    @SerialName("photo_id")
    val photoId: Long,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("analysis_status")
    val analysisStatus: DiaryAnalysisStatusResponse? = null,
)
