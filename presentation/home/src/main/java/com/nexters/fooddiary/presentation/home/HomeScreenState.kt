package com.nexters.fooddiary.presentation.home

import com.airbnb.mvrx.MavericksState
import java.time.LocalDate

data class HomeScreenState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weeklyPhotosByDate: Map<LocalDate, List<String>> = emptyMap(),
    val loadedWeekStartDate: LocalDate? = null,
) : MavericksState
