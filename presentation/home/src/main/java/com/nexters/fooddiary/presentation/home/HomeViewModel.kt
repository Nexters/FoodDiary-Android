package com.nexters.fooddiary.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.Collections.emptyMap

sealed interface HomeEvent {
    data class NavigateToDetail(val date: LocalDate) : HomeEvent
}

class HomeViewModel @AssistedInject constructor(
    @ApplicationContext context: Context,
    @Assisted initialState: HomeScreenState,
) : MavericksViewModel<HomeScreenState>(initialState) {
    private val _photoCountByDate = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val photoCountByDate: StateFlow<Map<LocalDate, Int>> = _photoCountByDate.asStateFlow()
    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        if (PermissionUtil.hasMediaPermission(context)) { }
    }

    fun onDateSelected(date: LocalDate) {
        setState { copy(selectedDate = date) }
    }

    fun onCardStackClicked() {
        withState { state ->
            _events.tryEmit(HomeEvent.NavigateToDetail(state.selectedDate))
        }
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
