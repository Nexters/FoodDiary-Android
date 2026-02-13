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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DetailState(
    val selectedDate: LocalDate = LocalDate.now(),
    val mealsByDate: Map<LocalDate, DailyMeals> = emptyMap(),
    val loadMealsRequest: Async<Unit> = Uninitialized,  // 식사 데이터 로딩 상태
) : MavericksState

sealed interface DetailEvent {
    data class CopyMapLink(val mapLink: String) : DetailEvent
    data class ShareMapLink(val place: String, val mapLink: String) : DetailEvent
    data object ShareLinkEmpty : DetailEvent
}

class DetailViewModel @AssistedInject constructor(
    @Assisted initialState: DetailState,
    private val getDiaryByDateUseCase: GetDiaryByDateUseCase,
) : MavericksViewModel<DetailState>(initialState) {
    private val _events = MutableSharedFlow<DetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DetailEvent> = _events.asSharedFlow()

    private inline fun executeAsync(
        crossinline action: suspend () -> Unit,
        crossinline updateState: DetailState.(Async<Unit>) -> DetailState
    ) = suspend { action() }.execute { result ->
        updateState(result)
    }

    fun loadMealsForDate(date: LocalDate, forceRefresh: Boolean = false) {
        withState { state ->
            if (!forceRefresh && state.mealsByDate.containsKey(date)) {
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
        // TODO: 수정 화면으로 이동
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

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DetailViewModel, DetailState> {
        override fun create(state: DetailState): DetailViewModel
    }

    companion object : MavericksViewModelFactory<DetailViewModel, DetailState> by hiltMavericksViewModelFactory()
}
