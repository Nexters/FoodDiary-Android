package com.nexters.fooddiary.presentation.detail

data class MealUiModel(
    val id: String,
    val dateString: String,    // ISO-8601: "2026-01-16"
    val mealType: String,      // "아침", "점심", "저녁"
    val time: String,          // "07:00"
    val location: String,      // "마포구"
    val place: String,         // "호진이네"
    val category: String,      // "중식"
    val keywords: List<String>, // ["#양장피", "#어향동고"]
    val imageUrls: List<String>, // 여러 이미지 URL 목록
    val isEmpty: Boolean,       // Empty 상태 플래그
    val isPending: Boolean,     // Pending 상태 플래그 (AI 분석 중)
)
