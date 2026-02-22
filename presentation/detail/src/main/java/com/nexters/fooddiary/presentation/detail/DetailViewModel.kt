package com.nexters.fooddiary.presentation.detail

import com.airbnb.mvrx.Async
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

data class DetailState(
    val selectedDate: LocalDate = LocalDate.now(),
    val mealsByDate: Map<LocalDate, DailyMeals> = emptyMap(),
    val loadMealsRequest: Async<DailyMeals> = Uninitialized,  // 식사 데이터 로딩 상태
) : MavericksState

sealed interface DetailEvent {
    data class CopyMapLink(val mapLink: String) : DetailEvent
    data class ShareMapLink(val place: String, val mapLink: String) : DetailEvent
    data object ShareLinkEmpty : DetailEvent
    data object NavigateToImagePicker : DetailEvent
}

class DetailViewModel @AssistedInject constructor(
    @Assisted initialState: DetailState,
    private val getDiaryByDateUseCase: GetDiaryByDateUseCase,
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
        syncSelectedDate(LocalDate.parse(dateString))
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
        // TODO: 수정 화면으로 이동
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
