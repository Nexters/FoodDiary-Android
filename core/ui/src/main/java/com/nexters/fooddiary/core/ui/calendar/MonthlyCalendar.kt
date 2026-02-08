package com.nexters.fooddiary.core.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar

// ... (existing code omitted for brevity in prompt, but I need to be careful with range)
// Actually I shouldn't replace the whole middle part if it's large.
// I will target the imports block and the preview block separately. 
// But replace_file_content can only handle one block at a time unless I use multi_replace.
// I will use multi_replace.

import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.nexters.fooddiary.core.ui.calendar.theme.CalendarColors
import com.nexters.fooddiary.core.ui.calendar.theme.calendarColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Collections.emptyMap
import java.util.Locale

@Composable
fun MonthlyCalendar(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    locale: Locale = Locale.getDefault(),
    colors: CalendarColors = calendarColors(),
    onMonthChanged: (YearMonth) -> Unit = {},
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
) {
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = remember { derivedStateOf { calendarState.firstVisibleMonth.yearMonth } }

    // 월 변경 감지 및 콜백 호출
    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.firstVisibleMonth.yearMonth }
            .distinctUntilChanged()
            .collect { yearMonth ->
                onMonthChanged(yearMonth)
            }
    }

    Column(modifier = modifier) {
        // 월/년도 헤더 화살표
        MonthCalendarHeader(
            yearMonth = visibleMonth.value,
            locale = locale,
            colors = colors,
            onPreviousClick = {
                coroutineScope.launch {
                    calendarState.animateScrollToMonth(calendarState.firstVisibleMonth.yearMonth.minusMonths(1))
                }
            },
            onNextClick = {
                coroutineScope.launch {
                    calendarState.animateScrollToMonth(calendarState.firstVisibleMonth.yearMonth.plusMonths(1))
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 요일 헤더
        MonthWeekDaysHeader(
            locale = locale,
            firstDayOfWeek = calendarState.firstDayOfWeek,
            colors = colors
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 월간 캘린더
        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                val photoCount = photoCountByDate[day.date] ?: 0
                MonthDayCell(
                    day = day,
                    isSelected = day.date == selectedDate,
                    photoCount = photoCount,
                    colors = colors,
                    onClick = {
                        // 다른 월의 날짜를 클릭한 경우 애니메이션 후 선택
                        if (day.position != DayPosition.MonthDate) {
                            coroutineScope.launch {
                                calendarState.animateScrollToMonth(YearMonth.from(day.date))
                                onDateSelected(day.date)
                            }
                        } else {
                            // 현재 월의 날짜는 즉시 선택
                            onDateSelected(day.date)
                        }
                    }
                )
            }
        )
    }
}

@Composable
private fun MonthCalendarHeader(
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
private fun MonthWeekDaysHeader(
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
private fun MonthDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    photoCount: Int,
    colors: CalendarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate

    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable(onClick = onClick)
            .padding(4.dp)
            .background(
                color = if (isSelected) colors.selectedBackground else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Date number
            Text(
                text = day.date.dayOfMonth.toString(),
                fontSize = 16.sp,
                color = when {
                    !isCurrentMonth -> colors.dayTextDisabled
                    isSelected -> colors.dayTextSelected
                    else -> colors.dayText
                },
                modifier = Modifier.padding(top = 8.dp)
            )

            // Inner box (from FD-19) with photo count overlay (from FD-29)
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .background(
                        color = if (isSelected) colors.selectedInnerBox else colors.unselectedInnerBox,
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Photo count badge (from FD-29)
                if (photoCount > 0 && isCurrentMonth) {
                    Text(
                        text = photoCount.toString(),
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF222222)
@Composable
private fun MonthlyCalendarPreview() {
    val state = rememberMonthCalendarState(
        selectedDate = LocalDate.now(),
        firstDayOfWeek = DayOfWeek.SUNDAY
    )
    
    MonthlyCalendar(
        calendarState = state,
        selectedDate = LocalDate.now(),
        onDateSelected = {},
        photoCountByDate = mapOf(
            LocalDate.now() to 3,
            LocalDate.now().minusDays(2) to 1
        )
    )
}
