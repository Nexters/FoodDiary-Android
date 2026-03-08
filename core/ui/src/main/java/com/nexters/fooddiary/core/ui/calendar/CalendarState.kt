package com.nexters.fooddiary.core.ui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

const val CALENDAR_YEAR_RANGE = 10

@Composable
fun rememberMonthCalendarState(
    selectedDate: LocalDate = LocalDate.now(),
    yearRange: Int = CALENDAR_YEAR_RANGE,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): CalendarState {
    val currentYear = remember { YearMonth.now().year }
    val startMonth = remember(yearRange) { YearMonth.of(currentYear - yearRange, 1) }
    val endMonth = remember(yearRange) { YearMonth.of(currentYear + yearRange, 12) }

    return rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(selectedDate),
        firstDayOfWeek = firstDayOfWeek
    )
}

@Composable
fun rememberWeeklyCalendarState(
    selectedDate: LocalDate = LocalDate.now(),
    adjacentMonths: Long = 500,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): WeekCalendarState {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember(adjacentMonths) { currentMonth.minusMonths(adjacentMonths) }
    val endMonth = remember(adjacentMonths) { currentMonth.plusMonths(adjacentMonths) }

    return rememberWeekCalendarState(
        startDate = startMonth.atDay(1),
        endDate = endMonth.atEndOfMonth(),
        firstVisibleWeekDate = selectedDate,
        firstDayOfWeek = firstDayOfWeek
    )
}
