package com.nexters.fooddiary.presentation.detail

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.usecase.GetDiaryByDateUseCase
import com.nexters.fooddiary.presentation.detail.util.toDailyMeals
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate
import java.time.LocalDateTime

data class DetailState(
    val selectedDate: LocalDate = LocalDate.now(),
    val mealsByDate: Map<LocalDate, DailyMeals> = emptyMap(),
    val loadMealsRequest: Async<DailyMeals> = Uninitialized,  // 식사 데이터 로딩 상태
    val loadingDates: Set<LocalDate> = emptySet(),
    val isPullRefreshing: Boolean = false,
) : MavericksState

sealed interface DetailEvent {
    data class CopyMapLink(val mapLink: String) : DetailEvent
    data class ShareMapLink(val place: String, val mapLink: String) : DetailEvent
    data object ShareLinkEmpty : DetailEvent
    data object NavigateToImagePicker : DetailEvent
    data class NavigateToModify(val diaryId: String) : DetailEvent
}

class DetailViewModel @AssistedInject constructor(
    @Assisted initialState: DetailState,
    private val getDiaryByDateUseCase: GetDiaryByDateUseCase,
) : MavericksViewModel<DetailState>(initialState) {
    private val _events = MutableSharedFlow<DetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DetailEvent> = _events.asSharedFlow()

    fun loadMealsForDate(
        date: LocalDate,
        forceRefresh: Boolean = false,
        isPullRefresh: Boolean = false,
    ) {
        withState { state ->
            if (state.loadingDates.contains(date)) return@withState
            if (!forceRefresh && state.mealsByDate.containsKey(date)) return@withState

            setState {
                copy(
                    loadingDates = loadingDates + date,
                    isPullRefreshing = if (isPullRefresh) true else isPullRefreshing,
                )
            }

            suspend {
                val diary = getDiaryByDateUseCase(date)
                diary.toDailyMeals(date)
            }.execute { result ->
                when (result) {
                    is Loading -> copy(loadMealsRequest = result)
                    is Success -> {
                        copy(
                            mealsByDate = putAndTrim(mealsByDate, date, result()),
                            loadMealsRequest = result,
                            loadingDates = loadingDates - date,
                            isPullRefreshing = if (isPullRefresh) false else isPullRefreshing,
                        )
                    }
                    else -> copy(
                        loadMealsRequest = result,
                        loadingDates = loadingDates - date,
                        isPullRefreshing = if (isPullRefresh) false else isPullRefreshing,
                    )
                }
            }
        }
    }

    fun invalidateMealsForDate(date: LocalDate) {
        setState {
            copy(mealsByDate = mealsByDate - date)
        }
    }

    fun refreshMealsForDate(date: LocalDate) {
        loadMealsForDate(date = date, forceRefresh = true)
    }

    fun onPullToRefresh() {
        withState { state ->
            loadMealsForDate(
                date = state.selectedDate,
                forceRefresh = true,
                isPullRefresh = true,
            )
        }
    }

    fun syncSelectedDate(dateString: String) {
        dateString.toLocalDateOrNull()?.let { parsedDate ->
            syncSelectedDate(parsedDate)
        }
    }

    fun syncSelectedDate(date: LocalDate) {
        withState { state ->
            if (state.selectedDate == date) return@withState
            setState { copy(selectedDate = date) }
        }
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

    fun onAddPhoto(slot: MealSlot, date: LocalDate) {
        _events.tryEmit(DetailEvent.NavigateToImagePicker)
    }

    fun onEditClick(slot: MealSlot, date: LocalDate) {
        withState { state ->
            val meals = state.mealsByDate[date] ?: return@withState
            val selectedMeal = when (slot) {
                MealSlot.BREAKFAST -> meals.breakfast
                MealSlot.LUNCH -> meals.lunch
                MealSlot.DINNER -> meals.dinner
                MealSlot.SNACK -> meals.snack
            }
            val diaryId = selectedMeal.diaryId?.takeIf { it.isNotBlank() } ?: return@withState
            _events.tryEmit(DetailEvent.NavigateToModify(diaryId))
        }
    }

    fun onDeleteClick() {
        // TODO: 삭제 확인/삭제 API 연동
    }

    fun onCopyClick(mapLink: String) {
        if (mapLink.isBlank()) return
        _events.tryEmit(DetailEvent.CopyMapLink(mapLink))
    }

    fun onShareClick(place: String, mapLink: String) {
        if (mapLink.isBlank()) {
            _events.tryEmit(DetailEvent.ShareLinkEmpty)
            return
        }
        _events.tryEmit(DetailEvent.ShareMapLink(place = place, mapLink = mapLink))
    }

    private fun putAndTrim(
        currentMealsByDate: Map<LocalDate, DailyMeals>,
        date: LocalDate,
        meals: DailyMeals,
    ): Map<LocalDate, DailyMeals> {
        val updated = LinkedHashMap(currentMealsByDate)
        updated.remove(date)
        updated[date] = meals

        while (updated.size > MAX_MEALS_CACHE_SIZE) {
            val oldestDate = updated.entries.first().key
            updated.remove(oldestDate)
        }

        return updated
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DetailViewModel, DetailState> {
        override fun create(state: DetailState): DetailViewModel
    }

    companion object : MavericksViewModelFactory<DetailViewModel, DetailState> by hiltMavericksViewModelFactory() {
        private const val MAX_MEALS_CACHE_SIZE = 14
    }
}

private fun String.toLocalDateOrNull(): LocalDate? {
    return runCatching { LocalDate.parse(this) }
        .getOrElse {
            runCatching { LocalDateTime.parse(this).toLocalDate() }.getOrNull()
        }
}
