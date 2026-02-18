package com.nexters.fooddiary.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class DiaryDetail(
    val date: LocalDate,
    val diaries: List<DiaryEntry>,
)

data class DiaryEntry(
    val diaryId: Long,
    val mealType: MealType,
    val analysisStatus: AnalysisStatus,
    val restaurantName: String?,
    val category: String?,
    val location: String?,
    val tags: List<String>,
    val coverPhotoUrl: String?,
    val mapLink: String?,
    val photoCount: Int,
    val photos: List<DiaryPhoto>,
)

data class DiaryPhoto(
    val photoId: Long,
    val imageUrl: String,
    val takenAt: LocalDateTime?,
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
}

enum class AnalysisStatus {
    DONE,
    PROCESSING,
}
