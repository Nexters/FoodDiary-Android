package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryDetailResponse(
    @SerialName("diaries")
    val diaries: List<DiarySummaryResponse>,
)

@Serializable
data class DiarySummaryResponse(
    @SerialName("id")
    val diaryId: Long,
    @SerialName("diary_date")
    val diaryDate: String? = null,
    @SerialName("time_type")
    val timeType: DiaryMealTypeResponse? = null,
    @SerialName("analysis_status")
    val analysisStatus: DiaryAnalysisStatusResponse? = null,
    @SerialName("restaurant_name")
    val restaurantName: String?,
    @SerialName("restaurant_url")
    val restaurantUrl: String? = null,
    @SerialName("category")
    val category: String?,
    @SerialName("road_address")
    val roadAddress: String? = null,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
    @SerialName("cover_photo_url")
    val coverPhotoUrl: String?,
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
