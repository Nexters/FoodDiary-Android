package com.nexters.fooddiary.presentation.home

import android.content.Context
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.domain.usecase.GetDiaryByMonthUseCase
import com.nexters.fooddiary.domain.usecase.GetDiarySummaryUseCase
import com.nexters.fooddiary.domain.usecase.GetDiariesSummaryUseCase
import com.nexters.fooddiary.domain.usecase.GetFoodPhotoCountByWeekUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.util.Collections.emptyMap

sealed interface HomeEvent {
    data class NavigateToDetail(val date: LocalDate) : HomeEvent
}

class HomeViewModel @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted initialState: HomeScreenState,
    private val getDiaryByMonthUseCase: GetDiaryByMonthUseCase,
    private val getDiarySummaryUseCase: GetDiarySummaryUseCase,
    private val getFoodPhotoCountByWeekUseCase: GetFoodPhotoCountByWeekUseCase,
    private val getDiariesSummaryUseCase: GetDiariesSummaryUseCase,
) : MavericksViewModel<HomeScreenState>(initialState) {
    private val _photoCountByDate = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val photoCountByDate: StateFlow<Map<LocalDate, Int>> = _photoCountByDate.asStateFlow()
    private val _photoUrlsByDate = MutableStateFlow<Map<LocalDate, List<String>>>(emptyMap())
    val photoUrlsByDate: StateFlow<Map<LocalDate, List<String>>> = _photoUrlsByDate.asStateFlow()
    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()
    private var loadSummaryJob: Job? = null

    private val initialSelectedDate: LocalDate = initialState.selectedDate

    init {
        loadSummaryForSelectedWeek()
    }

    /** 첫 화면 그린 뒤 호출. 캘린더 summary 즉시, 이번 주 개수(ML)는 yield 후 요청. */
    fun loadInitialData() {
        loadPhotosForMonth(YearMonth.from(initialSelectedDate))
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

    fun loadPhotosForMonth(yearMonth: YearMonth) {
        suspend {
            withContext(Dispatchers.IO) {
                val startDate = yearMonth.atDay(1)
                val endDate = yearMonth.atEndOfMonth()
                getDiariesSummaryUseCase(startDate, endDate)
            }
        }.execute { async ->
            val urlsByDate = async.invoke() ?: return@execute this
            _photoCountByDate.value = urlsByDate.mapValues { (_, photos) -> photos.size }
            _photoUrlsByDate.value = urlsByDate
            copy(diaryCountByDate = urlsByDate.mapValues { (_, photos) -> if (photos.isNotEmpty()) 1 else 0 })
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
            if (!shouldLoadWeek(state.selectedDate, state.loadedWeekStartDate)) return@withState

            loadSummaryJob?.cancel()
            loadSummaryJob = viewModelScope.launch {
                val summaryByDate = runCatching {
                    getDiarySummaryUseCase(
                        startDate = weekStart,
                        endDate = weekStart.plusDays(6),
                    )
                }.getOrDefault(emptyMap())

                setState {
                    if (weekStartOf(selectedDate) != weekStart) {
                        this
                    } else {
                        copy(
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
        override fun create(initialState: HomeScreenState): HomeViewModel
    }

    companion object : MavericksViewModelFactory<HomeViewModel, HomeScreenState> by hiltMavericksViewModelFactory()
}

internal fun weekStartOf(
    date: LocalDate,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): LocalDate = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))

internal fun shouldLoadWeek(
    selectedDate: LocalDate,
    loadedWeekStartDate: LocalDate?,
): Boolean = loadedWeekStartDate != weekStartOf(selectedDate)
