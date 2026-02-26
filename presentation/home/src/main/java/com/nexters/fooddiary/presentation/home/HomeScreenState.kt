package com.nexters.fooddiary.presentation.home

import com.airbnb.mvrx.MavericksState
import com.nexters.fooddiary.core.ui.food.FoodImageState
import java.time.LocalDate

data class HomeScreenState(
    val selectedDate: LocalDate = LocalDate.now(),
    val diaryCountByDate: Map<LocalDate, Int> = emptyMap(),
    val diaryCountByWeek: Int = 0,
    val weeklyPhotosByDate: Map<LocalDate, List<String>> = emptyMap(),
    val selectedDateImageState: FoodImageState = FoodImageState.Ready(
        timeText = "",
        locationText = "",
    ),
    val selectedDateImageStatesByUrl: Map<String, FoodImageState> = emptyMap(),
    val loadedWeekStartDate: LocalDate? = null,
) : MavericksState
