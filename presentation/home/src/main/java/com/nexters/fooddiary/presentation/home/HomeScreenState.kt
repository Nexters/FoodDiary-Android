package com.nexters.fooddiary.presentation.home

import com.airbnb.mvrx.MavericksState
import java.time.LocalDate

data class HomeScreenState(
    val selectedDate: LocalDate = LocalDate.now(),
    val isMonthlyCalendarView: Boolean = false,
    val weeklyPhotosByDate: Map<LocalDate, List<String>> = emptyMap(),
    val loadedWeekStartDate: LocalDate? = null,
) : MavericksState
