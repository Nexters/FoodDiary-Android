package com.nexters.fooddiary.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class DiaryDetail(
    val date: LocalDate,
    val note: String,
    val photos: List<DiaryPhotoDetail>,
)

data class DiaryPhotoDetail(
    val photoId: Long,
    val imageUrl: String,
    val takenAt: LocalDateTime?,
    val location: String?,
    val restaurantName: String?,
    val menuName: String?,
    val menuPrice: Int?,
    val mapLink: String?,
    val isProcessing: Boolean,
    val mealType: MealType,
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    UNKNOWN,
}
