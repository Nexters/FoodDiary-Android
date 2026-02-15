package com.nexters.fooddiary.presentation.home

import android.content.Context
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.domain.usecase.GetDiaryByMonthUseCase
import com.nexters.fooddiary.domain.usecase.GetFoodPhotoCountByWeekUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.time.LocalDate
import java.time.YearMonth

sealed interface HomeEvent {
    data class NavigateToDetail(val date: LocalDate) : HomeEvent
}

class HomeViewModel @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted startState: HomeScreenState,
    private val getFoodPhotoCountByWeekUseCase: GetFoodPhotoCountByWeekUseCase,
    private val getDiaryByMonthUseCase: GetDiaryByMonthUseCase,
) : MavericksViewModel<HomeScreenState>(startState) {
    private val _photoCountByDate = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val photoCountByDate: StateFlow<Map<LocalDate, Int>> = _photoCountByDate.asStateFlow()
    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private val initialSelectedDate: LocalDate = startState.selectedDate

    /** 첫 화면 그린 뒤 호출. 다이어리 즉시, 이번 주 개수(ML)는 yield 후 요청. */
    fun loadInitialData() {
        loadDiaryForMonth(YearMonth.from(initialSelectedDate))
        if (PermissionUtil.hasMediaPermission(context)) {
            scheduleWeekCountLoadAfterYield()
        }
    }

    /** yield 후 이번 주 개수 로딩 (메인 스레드 경쟁 완화) */
    private fun scheduleWeekCountLoadAfterYield() {
        viewModelScope.launch {
            yield()
            loadThisWeekPhotoCount()
        }
    }

    fun loadPhotosForMonth(yearMonth: YearMonth) = loadDiaryForMonth(yearMonth)

    private fun loadDiaryForMonth(yearMonth: YearMonth) {
        suspend {
            withContext(Dispatchers.IO) {
                val diaries = getDiaryByMonthUseCase(yearMonth)
                diaries.keys.associateWith { 1 }
            }
        }.execute { async ->
            copy(diaryCountByDate = async.invoke() ?: emptyMap())
        }
    }

    private fun loadThisWeekPhotoCount() {
        suspend {
            withContext(Dispatchers.Default) { getFoodPhotoCountByWeekUseCase() }
        }.execute { async ->
            copy(diaryCountByWeek = async.invoke() ?: 0)
        }
    }

    fun onDateSelected(date: LocalDate) {
        setState { copy(selectedDate = date) }
        _events.tryEmit(HomeEvent.NavigateToDetail(date))
    }

    fun onToggleCalendarView() {
        setState { copy(isMonthlyCalendarView = !isMonthlyCalendarView) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeScreenState> {
        override fun create(state: HomeScreenState): HomeViewModel
    }

    companion object : MavericksViewModelFactory<HomeViewModel, HomeScreenState> by hiltMavericksViewModelFactory()
}
