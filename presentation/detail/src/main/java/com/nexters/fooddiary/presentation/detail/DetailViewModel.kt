package com.nexters.fooddiary.presentation.detail

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.usecase.GetDiaryByDateUseCase
import com.nexters.fooddiary.domain.usecase.diary.DeleteDiaryUseCase
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
    val deleteRequest: Async<Unit> = Uninitialized,
) : MavericksState

sealed interface DetailEvent {
    data class CopyMapLink(val mapLink: String) : DetailEvent
    data class ShareMapLink(val place: String, val mapLink: String) : DetailEvent
    data object ShareLinkEmpty : DetailEvent
    data object NavigateToImagePicker : DetailEvent
    data class NavigateToModify(val diaryId: String) : DetailEvent
    data class DeleteSuccess(val date: LocalDate) : DetailEvent
    data object DeleteEmpty : DetailEvent
    data object DeleteFailed : DetailEvent
}

class DetailViewModel @AssistedInject constructor(
    @Assisted initialState: DetailState,
    private val getDiaryByDateUseCase: GetDiaryByDateUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
) : MavericksViewModel<DetailState>(initialState) {
    private val _events = MutableSharedFlow<DetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DetailEvent> = _events.asSharedFlow()

    fun loadMealsForDate(date: LocalDate, forceRefresh: Boolean = false) {
        withState { state ->
            if (!forceRefresh && state.mealsByDate.containsKey(date)) return@withState

            suspend {
                val diary = getDiaryByDateUseCase(date)
                diary.toDailyMeals(date)
            }.execute { result ->
                when (result) {
                    is Success -> {
                        copy(
                            mealsByDate = putAndTrim(mealsByDate, date, result()),
                            loadMealsRequest = result,
                        )
                    }
                    else -> copy(loadMealsRequest = result)
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
        withState { state ->
            if (state.deleteRequest is Loading) return@withState

            val selectedDate = state.selectedDate
            val diaryIds = state.mealsByDate[selectedDate]
                ?.asOrderedList()
                .orEmpty()
                .mapNotNull { it.diaryId?.toIntOrNull() }
                .distinct()

            if (diaryIds.isEmpty()) {
                _events.tryEmit(DetailEvent.DeleteEmpty)
                return@withState
            }

            suspend {
                diaryIds.forEach { diaryId ->
                    deleteDiaryUseCase(diaryId)
                }
            }.execute { result ->
                when (result) {
                    is Success -> {
                        refreshMealsForDate(selectedDate)
                        _events.tryEmit(DetailEvent.DeleteSuccess(selectedDate))
                        copy(deleteRequest = result)
                    }
                    is Fail -> {
                        _events.tryEmit(DetailEvent.DeleteFailed)
                        copy(deleteRequest = result)
                    }
                    else -> copy(deleteRequest = result)
                }
            }
        }
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
