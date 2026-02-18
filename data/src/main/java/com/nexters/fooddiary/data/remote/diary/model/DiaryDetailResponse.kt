package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryDetailResponseByDate(
    @SerialName("diaries")
    val diaries: List<DiarySummaryResponse>,
)

@Serializable
data class DiarySummaryResponse(
    @SerialName("diary_id")
    val diaryId: Long,
    @SerialName("time_type")
    val timeType: DiaryMealTypeResponse? = null,
    @SerialName("analysis_status")
    val analysisStatus: DiaryAnalysisStatusResponse? = null,
    @SerialName("restaurant_name")
    val restaurantName: String?,
    @SerialName("category")
    val category: String?,
    @SerialName("location")
    val location: String? = null,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
    @SerialName("cover_photo_url")
    val coverPhotoUrl: String?,
    @SerialName("maplink")
    val mapLink: String? = null,
    @SerialName("photo_count")
    val photoCount: Int?,
    @SerialName("photos")
    val photos: List<DiaryPhoto>,
)

@Serializable
enum class DiaryMealTypeResponse {
    @SerialName("breakfast")
    BREAKFAST,

    @SerialName("lunch")
    LUNCH,

    @SerialName("dinner")
    DINNER,
}

@Serializable
enum class DiaryAnalysisStatusResponse {
    @SerialName("done")
    DONE,

    @SerialName("processing")
    PROCESSING,
}
