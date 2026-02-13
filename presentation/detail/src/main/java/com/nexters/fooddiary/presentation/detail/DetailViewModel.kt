package com.nexters.fooddiary.presentation.detail

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryPhotoDetail
import com.nexters.fooddiary.domain.model.MealType
import com.nexters.fooddiary.domain.usecase.GetDiaryByDateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DetailState(
    val selectedDateString: String = LocalDate.now().toString(),  // ISO-8601: "2026-01-16"
    val dailyMeals: Map<String, List<MealUiModel>> = emptyMap(),  // Key: ISO-8601 date string
    val loadMealsRequest: Async<Unit> = Uninitialized,  // 식사 데이터 로딩 상태
) : MavericksState

class DetailViewModel @AssistedInject constructor(
    @Assisted initialState: DetailState,
    private val getDiaryByDateUseCase: GetDiaryByDateUseCase,
) : MavericksViewModel<DetailState>(initialState) {

    private inline fun executeAsync(
        crossinline action: suspend () -> Unit,
        crossinline updateState: DetailState.(Async<Unit>) -> DetailState
    ) = suspend { action() }.execute { result ->
        updateState(result)
    }

    fun loadMealsForDate(dateString: String) {
        withState { state ->
            if (state.dailyMeals.containsKey(dateString)) {
                return@withState
            }
        }

        executeAsync(
            action = {
                val diary = getDiaryByDateUseCase(LocalDate.parse(dateString))
                val meals = diary.toMealUiModels(dateString)
                setState {
                    copy(dailyMeals = dailyMeals + (dateString to meals))
                }
            },
            updateState = { copy(loadMealsRequest = it) }
        )
    }

    fun syncSelectedDate(dateString: String) {
        withState { state ->
            if (state.selectedDateString == dateString) return@withState
        }
        setState { copy(selectedDateString = dateString) }
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

private fun DiaryDetail.toMealUiModels(dateString: String): List<MealUiModel> {
    val photosByMeal = photos.groupBy { it.mealType }
    return listOf(
        MealType.BREAKFAST.toMealUiModel(dateString, photosByMeal[MealType.BREAKFAST].orEmpty()),
        MealType.LUNCH.toMealUiModel(dateString, photosByMeal[MealType.LUNCH].orEmpty()),
        MealType.DINNER.toMealUiModel(dateString, photosByMeal[MealType.DINNER].orEmpty()),
    )
}

private fun MealType.toMealUiModel(
    dateString: String,
    photos: List<DiaryPhotoDetail>,
): MealUiModel {
    if (photos.isEmpty()) {
        return MealUiModel(
            id = "${dateString}_${name.lowercase()}",
            dateString = dateString,
            mealType = displayName(),
            time = "",
            location = "",
            place = "",
            keywords = emptyList(),
            imageUrls = emptyList(),
            isEmpty = true,
            isPending = false,
        )
    }

    val firstPhoto = photos.first()
    val imageUrls = photos.map { it.imageUrl }
    val menuKeyword = firstPhoto.menuName?.takeIf { it.isNotBlank() }?.let { "#$it" }

    return MealUiModel(
        id = "${dateString}_${name.lowercase()}",
        dateString = dateString,
        mealType = displayName(),
        time = firstPhoto.takenAt.format(DateTimeFormatter.ofPattern("HH:mm")),
        location = firstPhoto.location.orEmpty(),
        place = firstPhoto.restaurantName.orEmpty(),
        keywords = listOfNotNull(menuKeyword),
        imageUrls = imageUrls,
        isEmpty = false,
        isPending = false,
    )
}

private fun MealType.displayName(): String {
    return when (this) {
        MealType.BREAKFAST -> "아침"
        MealType.LUNCH -> "점심"
        MealType.DINNER -> "저녁"
        MealType.UNKNOWN -> "기타"
    }
}
