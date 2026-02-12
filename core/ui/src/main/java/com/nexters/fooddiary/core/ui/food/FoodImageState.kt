package com.nexters.fooddiary.core.ui.food

sealed interface FoodImageState {

    data class FullUI(
        val timeText: String,           // 07:00
        val locationText: String,       // 마포구
        val placeText: String,          // 호진이네
        val keywords: List<String>,     // #양장피 #어향동고
        val onSaveClick: () -> Unit,    // 저장 버튼
        val onShareClick: () -> Unit,   // 공유 버튼
    ) : FoodImageState

    data class Summary(
        val timeText: String,           // 07:00
        val locationText: String,       // 마포구
    ) : FoodImageState

    data object Pending : FoodImageState  // AI 분석 중 상태
}
