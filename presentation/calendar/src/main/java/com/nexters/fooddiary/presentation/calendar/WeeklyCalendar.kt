package com.nexters.fooddiary.presentation.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * 주단위 캘린더 컴포넌트
 *
 * @param modifier Modifier
 * @param selectedDate 선택된 날짜
 * @param onDateSelected 날짜 선택 콜백
 * @param adjacentMonths 현재 월 기준 앞뒤로 스크롤 가능한 개월 수 (기본값: 500개월 = 약 41년)
 */
@Composable
fun WeeklyCalendar(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
    adjacentMonths: Long = 500,
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(adjacentMonths) }
    val endMonth = remember { currentMonth.plusMonths(adjacentMonths) }

    val state = rememberWeekCalendarState(
        startDate = startMonth.atDay(1),
        endDate = endMonth.atEndOfMonth(),
        firstVisibleWeekDate = selectedDate,
        firstDayOfWeek = DayOfWeek.SUNDAY
    )

    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = remember { derivedStateOf {
        YearMonth.from(state.firstVisibleWeek.days.first().date)
    } }

    Column(modifier = modifier) {
        // 월/년도 헤더 with 화살표
        CalendarHeader(
            yearMonth = visibleMonth.value,
            onPreviousClick = {
                coroutineScope.launch {
                    val targetDate = state.firstVisibleWeek.days.first().date.minusWeeks(1)
                    state.animateScrollToWeek(targetDate)
                }
            },
            onNextClick = {
                coroutineScope.launch {
                    val targetDate = state.firstVisibleWeek.days.first().date.plusWeeks(1)
                    state.animateScrollToWeek(targetDate)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 요일 헤더
        WeekDaysHeader()

        Spacer(modifier = Modifier.height(8.dp))

        // 주간 캘린더
        WeekCalendar(
            state = state,
            dayContent = { day ->
                DayCell(
                    date = day.date,
                    isSelected = day.date == selectedDate,
                    onClick = { onDateSelected(day.date) }
                )
            }
        )
    }
}

/**
 * 캘린더 헤더 (월/년도 + 화살표)
 */
@Composable
private fun CalendarHeader(
    yearMonth: YearMonth,
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
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()} ${yearMonth.year}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Row {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous",
                    tint = Color.White
                )
            }
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * 요일 헤더
 */
@Composable
private fun WeekDaysHeader(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek> = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 날짜 셀
 */
@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .then(
                    if (isSelected) {
                        Modifier.border(2.dp, Color(0xFFE91E63), CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
