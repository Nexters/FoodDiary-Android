package com.nexters.fooddiary.core.ui.food

sealed interface FoodImageState {

    data class Ready(
        val timeText: String,           // 07:00
        val locationText: String,       // 마포구
    ) : FoodImageState

    data object Pending : FoodImageState  // AI 분석 중 상태
}
