package com.nexters.fooddiary.core.ui.food

sealed interface FoodImageState {

    data class Ready(
        val timeText: String,           // 07:00
        val locationText: String,       // 마포구
    ) : FoodImageState

    data object Processing : FoodImageState  // AI 분석 진행 중 (이미지 블러 + 오버레이)

    data object Pending : FoodImageState  // AI 분석 중 상태
}
