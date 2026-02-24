package com.nexters.fooddiary.core.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.calendar.theme.CalendarColors
import com.nexters.fooddiary.core.ui.calendar.theme.calendarColors
import com.nexters.fooddiary.core.ui.theme.Gray700
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

internal val CalendarContainerShape = RoundedCornerShape(16.dp)

@Composable
fun WeeklyCalendar(
    calendarState: WeekCalendarState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    locale: Locale = Locale.getDefault(),
    colors: CalendarColors = calendarColors(),
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
    today: LocalDate = LocalDate.now(),
) {
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = remember {
        derivedStateOf {
            YearMonth.from(calendarState.firstVisibleWeek.days.first().date)
        }
    }
    Column(modifier = modifier) {
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

        Column(
            modifier = Modifier
                .clip(CalendarContainerShape)
                .border(1.dp, color = Gray700.copy(alpha = 0.3f), CalendarContainerShape)
                .padding(vertical = 8.dp, horizontal = 12.dp),
        ) {
            WeekCalendar(
                state = calendarState,
                dayContent = { day ->
                    val photoCount = photoCountByDate[day.date] ?: 0
                    val isEnabled = !day.date.isAfter(today)
                    DayCell(
                        date = day.date,
                        isSelected = day.date == selectedDate,
                        hasData = photoCount > 0,
                        isEnabled = isEnabled,
                        locale = locale,
                        onClick = { if (isEnabled) onDateSelected(day.date) }
                    )
                }
            )
        }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable { onPreviousClick() },
            painter = painterResource(drawable.ic_back),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "Previous",
        )
        Text(
            modifier = Modifier.weight(1f),
            text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.headerText,
            textAlign = TextAlign.Center,
        )
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable { onNextClick() },
            painter = painterResource(drawable.ic_next),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "Next",
        )
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    hasData: Boolean,
    isEnabled: Boolean = true,
    locale: Locale,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeonStyleDay(
        topText = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
        bottomText = date.dayOfMonth.toString().padStart(2, '0'),
        showDot = hasData,
        isSelected = isSelected,
        modifier = modifier
            .heightIn(max = 56.dp)
            .then(if (isEnabled) Modifier.clickable(onClick = onClick) else Modifier.clickable(enabled = false) {})
            .then(if (!isEnabled) Modifier.alpha(0.4f) else Modifier)
    )
}

@Preview
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
        onDateSelected = {},
        photoCountByDate = mapOf(currentDate to 1)
    )
}
