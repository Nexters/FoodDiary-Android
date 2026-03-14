package com.nexters.fooddiary.domain.model

import java.time.LocalDate

data class DiaryDetail(
    val date: LocalDate,
    val diaries: List<DiaryEntry>,
)

data class DiaryEntry(
    val diaryId: Long,
    val diaryDate: String,
    val mealType: MealType,
    val analysisStatus: AnalysisStatus,
    val createdAt: String?,
    val restaurantName: String?,
    val category: String?,
    val addressName: String? = null,
    val roadAddress: String? = null,
    val location: String?,
    val tags: List<String>,
    val note: String? = null,
    val coverPhotoUrl: String?,
    val coverPhotoId: Long = 0L,
    val mapLink: String?,
    val photoCount: Int,
    val photos: List<DiaryPhoto>,
)

data class DiaryPhoto(
    val photoId: Long,
    val imageUrl: String,
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
}

enum class AnalysisStatus {
    DONE,
    PROCESSING,
    FAILED,
}
