package com.nexters.fooddiary.presentation.detail

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate

data class DetailState(
    val selectedDateString: String = LocalDate.now().toString(),  // ISO-8601: "2026-01-16"
    val dailyMeals: Map<String, List<MealUiModel>> = emptyMap(),  // Key: ISO-8601 date string
    val loadMealsRequest: Async<Unit> = Uninitialized,  // 식사 데이터 로딩 상태
) : MavericksState

class DetailViewModel @AssistedInject constructor(
    @Assisted initialState: DetailState,
) : MavericksViewModel<DetailState>(initialState) {

    private inline fun executeAsync(
        crossinline action: suspend () -> Unit,
        crossinline updateState: DetailState.(Async<Unit>) -> DetailState
    ) = suspend { action() }.execute { result ->
        updateState(result)
    }

    fun loadMealsForDate(dateString: String) {
        executeAsync(
            action = {
                // TODO: 실제 API 호출 또는 Repository에서 데이터 로딩
                // val meals = repository.getMealsByDate(dateString)
                // setState { copy(dailyMeals = dailyMeals + (dateString to meals)) }
            },
            updateState = { copy(loadMealsRequest = it) }
        )
    }

    fun navigateToPreviousDay() {
        setState {
            val currentDate = LocalDate.parse(selectedDateString)
            val previousDate = currentDate.minusDays(1)
            copy(selectedDateString = previousDate.toString())
        }
    }

    fun navigateToNextDay() {
        setState {
            val currentDate = LocalDate.parse(selectedDateString)
            val nextDate = currentDate.plusDays(1)
            copy(selectedDateString = nextDate.toString())
        }
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
