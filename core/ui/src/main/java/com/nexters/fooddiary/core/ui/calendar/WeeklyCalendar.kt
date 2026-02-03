package com.nexters.fooddiary.presentation.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.nexters.fooddiary.core.ui.calendar.theme.CalendarColors
import com.nexters.fooddiary.core.ui.calendar.theme.calendarColors
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.White
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val CalendarContainerShape = RoundedCornerShape(16.dp)
private val DayDotShape = RoundedCornerShape(3.dp)

@Composable
fun WeeklyCalendar(
    calendarState: WeekCalendarState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    locale: Locale = Locale.getDefault(),
    colors: CalendarColors = calendarColors(),
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
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
                .shadow(4.dp, CalendarContainerShape, spotColor = Gray600.copy(alpha = 0.25f))
                .clip(CalendarContainerShape)
                .border(1.dp, Gray600.copy(alpha = 0.6f), CalendarContainerShape)
                .padding(16.dp),
        ) {
            WeekCalendar(
                state = calendarState,
                dayContent = { day ->
                    val photoCount = photoCountByDate[day.date] ?: 0
                    DayCell(
                        date = day.date,
                        isSelected = day.date == selectedDate,
                        hasData = photoCount > 0,
                        locale = locale,
                        colors = colors,
                        onClick = { onDateSelected(day.date) }
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
            imageVector = Icons.Default.ChevronLeft,
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
            imageVector = Icons.Default.ChevronRight,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "Next",
        )
    }
}

private val DayCellShape = RoundedCornerShape(8.dp)

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    hasData: Boolean,
    locale: Locale,
    colors: CalendarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (isSelected) Modifier.shadow(
                    4.dp,
                    DayCellShape,
                    spotColor = PrimBase.copy(alpha = 0.4f)
                ) else Modifier
            )
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) colors.selectedBackground else Color.Transparent,
                shape = DayCellShape
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (hasData) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (isSelected) White else PrimBase,
                        shape = DayDotShape,
                    ),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) colors.dayTextSelected else colors.weekdayText,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = date.dayOfMonth.toString().padStart(2, '0'),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) colors.dayTextSelected else colors.dayText,
        )
    }
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
        onDateSelected = {}
    )
}
