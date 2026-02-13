package com.nexters.fooddiary.presentation.detail

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.MealType
import com.nexters.fooddiary.domain.usecase.GetDiaryByDateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DetailState(
    val selectedDate: LocalDate = LocalDate.now(),
    val mealsByDate: Map<LocalDate, DailyMeals> = emptyMap(),
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

    fun loadMealsForDate(date: LocalDate) {
        withState { state ->
            if (state.mealsByDate.containsKey(date)) {
                return@withState
            }
        }

        executeAsync(
            action = {
                val diary = getDiaryByDateUseCase(date)
                val meals = diary.toDailyMeals(date)
                setState {
                    copy(mealsByDate = mealsByDate + (date to meals))
                }
            },
            updateState = { copy(loadMealsRequest = it) }
        )
    }

    fun syncSelectedDate(dateString: String) {
        syncSelectedDate(LocalDate.parse(dateString))
    }

    fun syncSelectedDate(date: LocalDate) {
        withState { state ->
            if (state.selectedDate == date) return@withState
        }
        setState { copy(selectedDate = date) }
    }

    fun navigateToPreviousDay() {
        setState {
            copy(selectedDate = selectedDate.minusDays(1))
        }
    }

    fun navigateToNextDay() {
        setState {
            copy(selectedDate = selectedDate.plusDays(1))
        }
    }

    fun onMealCardClick(slot: MealSlot, date: LocalDate) {
        // TODO: Navigate to image picker or detail
    }

    fun onEditClick(slot: MealSlot, date: LocalDate) {
        // TODO: Navigate to edit screen or show edit dialog
    }

    fun onCopyClick(slot: MealSlot, date: LocalDate) {
        // TODO: Implement copy functionality
    }

    fun onShareClick(slot: MealSlot, date: LocalDate) {
        // TODO: Implement share functionality
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DetailViewModel, DetailState> {
        override fun create(state: DetailState): DetailViewModel
    }

    companion object : MavericksViewModelFactory<DetailViewModel, DetailState> by hiltMavericksViewModelFactory()
}

private fun DiaryDetail.toDailyMeals(date: LocalDate): DailyMeals {
    val diaryByMeal = diaries.associateBy { it.mealType }
    return DailyMeals(
        breakfast = MealSlot.BREAKFAST.toMealUiModel(date, diaryByMeal[MealType.BREAKFAST]),
        lunch = MealSlot.LUNCH.toMealUiModel(date, diaryByMeal[MealType.LUNCH]),
        dinner = MealSlot.DINNER.toMealUiModel(date, diaryByMeal[MealType.DINNER]),
    )
}

private fun MealSlot.toMealUiModel(
    date: LocalDate,
    diary: DiaryEntry?,
): MealCardUiModel {
    if (diary == null) {
        return MealCardUiModel.empty(date, this)
    }

    val firstPhoto = diary.photos.firstOrNull()
    val imageUrls = diary.photos.map { it.imageUrl }
    return MealCardUiModel(
        id = "${date}_${name.lowercase()}",
        date = date,
        slot = this,
        time = firstPhoto?.takenAt?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty(),
        location = diary.location.orEmpty(),
        place = diary.restaurantName.orEmpty(),
        keywords = diary.tags,
        mapLink = diary.mapLink.orEmpty(),
        imageUrls = imageUrls,
        status = if (diary.analysisStatus == AnalysisStatus.PROCESSING) {
            MealCardStatus.PENDING
        } else {
            MealCardStatus.READY
        },
    )
}
