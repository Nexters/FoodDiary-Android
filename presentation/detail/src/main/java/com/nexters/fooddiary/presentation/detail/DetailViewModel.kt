package com.nexters.fooddiary.presentation.detail

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate

data class DetailState(
    val selectedDateString: String = LocalDate.now().toString(),  // ISO-8601: "2026-01-16"
    val dailyMeals: Map<String, List<MealUiModel>> = emptyMap(),  // Key: ISO-8601 date string
    val isLoading: Boolean = false,
) : MavericksState

class DetailViewModel @AssistedInject constructor(
    @Assisted initialState: DetailState,
) : MavericksViewModel<DetailState>(initialState) {

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val mockMeals = mapOf(
            today.toString() to listOf(
                MealUiModel(
                    id = "1",
                    dateString = today.toString(),
                    mealType = "아침",
                    time = "07:00",
                    location = "마포구",
                    place = "호진이네",
                    category = "중식",
                    keywords = listOf("#양장피", "#어향동고"),
                    imageUrls = listOf(
                        "https://picsum.photos/300/300?random=1",
                        "https://picsum.photos/300/300?random=2",
                        "https://picsum.photos/300/300?random=3"
                    ),
                    isEmpty = false,
                    isPending = false,
                ),
                MealUiModel(
                    id = "2",
                    dateString = today.toString(),
                    mealType = "점심",
                    time = "12:30",
                    location = "강남구",
                    place = "",
                    category = "",
                    keywords = emptyList(),
                    imageUrls = listOf("https://picsum.photos/300/300?random=4"),
                    isEmpty = false,
                    isPending = true, // AI 분석 중
                ),
                MealUiModel(
                    id = "3",
                    dateString = today.toString(),
                    mealType = "저녁",
                    time = "19:00",
                    location = "",
                    place = "",
                    category = "",
                    keywords = emptyList(),
                    imageUrls = emptyList(),
                    isEmpty = true, // 이미지 없음
                    isPending = false,
                ),
            ),
            yesterday.toString() to listOf(
                MealUiModel(
                    id = "4",
                    dateString = yesterday.toString(),
                    mealType = "점심",
                    time = "13:00",
                    location = "용산구",
                    place = "맛있는 집",
                    category = "한식",
                    keywords = listOf("#김치찌개", "#제육볶음"),
                    imageUrls = listOf(
                        "https://picsum.photos/300/300?random=5",
                        "https://picsum.photos/300/300?random=6"
                    ),
                    isEmpty = false,
                    isPending = false,
                ),
            )
        )

        setState { copy(dailyMeals = mockMeals) }
    }

    fun onMealCardClick(mealId: String) {
        // TODO: Navigate to image picker or detail
    }

    fun onEditClick(mealType: String, dateString: String) {
        // TODO: Navigate to edit screen or show edit dialog
    }

    fun onSaveClick(mealId: String) {
        // TODO: Implement save functionality
    }

    fun onShareClick(mealId: String) {
        // TODO: Implement share functionality
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DetailViewModel, DetailState> {
        override fun create(state: DetailState): DetailViewModel
    }

    companion object : MavericksViewModelFactory<DetailViewModel, DetailState> by hiltMavericksViewModelFactory()
}
