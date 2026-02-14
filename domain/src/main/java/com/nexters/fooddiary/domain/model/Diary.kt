package com.nexters.fooddiary.domain.model

import java.time.LocalDate

data class Diary(
    val date: LocalDate,
    val note: String,
    val photos: List<DiaryPhoto>
)

data class DiaryPhoto(
    val photoId: Long,
    val imageUrl: String,
    val takenAt: String,
    val restaurantName: String?,
    val menuName: String?,
    val menuPrice: Int?,
    val timeType: String?
)
