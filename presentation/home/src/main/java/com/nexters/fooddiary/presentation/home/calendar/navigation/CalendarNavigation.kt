package com.nexters.fooddiary.presentation.home.calendar.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.home.calendar.CalendarScreen
import kotlinx.serialization.Serializable

@Serializable
object CalendarRoute

fun NavGraphBuilder.calendarScreen() {
    composable<CalendarRoute> {
        CalendarScreen()
    }
}
