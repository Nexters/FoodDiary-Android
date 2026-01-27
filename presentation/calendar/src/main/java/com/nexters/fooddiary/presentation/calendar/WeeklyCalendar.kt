package com.nexters.fooddiary.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.fooddiary.presentation.calendar.theme.CalendarColors
import com.nexters.fooddiary.presentation.calendar.theme.calendarColors
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyCalendar(
    calendarState: WeekCalendarState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    locale: Locale = Locale.getDefault(),
    colors: CalendarColors = calendarColors(),
) {

    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = remember { derivedStateOf {
        YearMonth.from(calendarState.firstVisibleWeek.days.first().date)
    } }

    Column(modifier = modifier) {
        // 월/년도 헤더 with 화살표
        CalendarHeader(
            yearMonth = visibleMonth.value,
            locale = locale,
            colors = colors,
            onPreviousClick = {
                coroutineScope.launch {
                    val targetDate = calendarState.firstVisibleWeek.days.first().date.minusWeeks(1)
                    calendarState.animateScrollToWeek(targetDate)
                }
            },
            onNextClick = {
                coroutineScope.launch {
                    val targetDate = calendarState.firstVisibleWeek.days.first().date.plusWeeks(1)
                    calendarState.animateScrollToWeek(targetDate)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 요일 헤더
        WeekDaysHeader(
            locale = locale,
            firstDayOfWeek = calendarState.firstDayOfWeek,
            colors = colors
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 주간 캘린더
        WeekCalendar(
            state = calendarState,
            dayContent = { day ->
                DayCell(
                    date = day.date,
                    isSelected = day.date == selectedDate,
                    colors = colors,
                    onClick = { onDateSelected(day.date) }
                )
            }
        )
    }
}

@Composable
private fun CalendarHeader(
    yearMonth: YearMonth,
    locale: Locale,
    colors: CalendarColors,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, locale).uppercase()} ${yearMonth.year}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.headerText
        )

        Row {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous",
                    tint = colors.iconTint
                )
            }
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next",
                    tint = colors.iconTint
                )
            }
        }
    }
}

@Composable
private fun WeekDaysHeader(
    locale: Locale,
    firstDayOfWeek: DayOfWeek,
    colors: CalendarColors,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = remember(firstDayOfWeek) { daysOfWeek(firstDayOfWeek = firstDayOfWeek) }

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, locale).uppercase(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = colors.weekdayText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    colors: CalendarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) colors.selectedBackground else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) colors.selectedInnerBox else colors.dayText
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF222222)
@Composable
private fun WeeklyCalendarPreview() {
    val currentDate = LocalDate.now()
    val startDate = currentDate.minusDays(500)
    val endDate = currentDate.plusDays(500)
    val firstDayOfWeek = DayOfWeek.SUNDAY
    
    val state = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = firstDayOfWeek
    )
    
    WeeklyCalendar(
        calendarState = state,
        selectedDate = currentDate,
        onDateSelected = {}
    )
}
