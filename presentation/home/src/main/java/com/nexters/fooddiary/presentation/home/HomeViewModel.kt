package com.nexters.fooddiary.presentation.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.domain.usecase.GetDiarySummaryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Collections.emptyMap

sealed interface HomeEvent {
    data class NavigateToDetail(val date: LocalDate) : HomeEvent
}

class HomeViewModel @AssistedInject constructor(
    @ApplicationContext context: Context,
    @Assisted initialState: HomeScreenState,
    private val getDiarySummaryUseCase: GetDiarySummaryUseCase,
) : MavericksViewModel<HomeScreenState>(initialState) {
    private val _photoCountByDate = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val photoCountByDate: StateFlow<Map<LocalDate, Int>> = _photoCountByDate.asStateFlow()
    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()
    private var loadSummaryJob: Job? = null

    init {
        if (PermissionUtil.hasMediaPermission(context)) { }
        loadSummaryForSelectedWeek()
    }

    fun onDateSelected(date: LocalDate) {
        setState { copy(selectedDate = date) }
        loadSummaryForSelectedWeek()
    }

    fun onCardStackClicked() {
        withState { state ->
            _events.tryEmit(HomeEvent.NavigateToDetail(state.selectedDate))
        }
    }

    fun onToggleCalendarView() {
        setState { copy(isMonthlyCalendarView = !isMonthlyCalendarView) }
    }

    private fun loadSummaryForSelectedWeek() {
        withState { state ->
            val weekStart = weekStartOf(state.selectedDate)
            if (state.loadedWeekStartDate == weekStart) return@withState

            loadSummaryJob?.cancel()
            loadSummaryJob = viewModelScope.launch {
                val summaryByDate = runCatching {
                    getDiarySummaryUseCase(
                        startDate = weekStart,
                        endDate = weekStart.plusDays(6),
                    )
                }.getOrDefault(emptyMap())

                setState { currentState ->
                    if (weekStartOf(currentState.selectedDate) != weekStart) {
                        currentState
                    } else {
                        currentState.copy(
                            weeklyPhotosByDate = summaryByDate,
                            loadedWeekStartDate = weekStart,
                        )
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeScreenState> {
        override fun create(state: HomeScreenState): HomeViewModel
    }

    companion object : MavericksViewModelFactory<HomeViewModel, HomeScreenState> by hiltMavericksViewModelFactory()

}

internal fun weekStartOf(
    date: LocalDate,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): LocalDate = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
