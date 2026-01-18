package com.nexters.fooddiary.presentation.component.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * 월단위 캘린더 컴포넌트
 * 
 * @param modifier Modifier
 * @param selectedDate 선택된 날짜
 * @param onDateSelected 날짜 선택 콜백
 * @param adjacentMonths 현재 월 기준 앞뒤로 스크롤 가능한 개월 수
 */
@Composable
fun MonthlyCalendar(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
    adjacentMonths: Long = 500,
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(adjacentMonths) }
    val endMonth = remember { currentMonth.plusMonths(adjacentMonths) }
    
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(selectedDate),
        firstDayOfWeek = DayOfWeek.SUNDAY
    )
    
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = remember { derivedStateOf { state.firstVisibleMonth.yearMonth } }

    Column(modifier = modifier) {
        // 월/년도 헤더 화살표
        MonthCalendarHeader(
            yearMonth = visibleMonth.value,
            onPreviousClick = {
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.minusMonths(1))
                }
            },
            onNextClick = {
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.plusMonths(1))
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 요일 헤더
        MonthWeekDaysHeader()
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 월간 캘린더
        HorizontalCalendar(
            state = state,
            contentHeightMode = ContentHeightMode.Fill,
            dayContent = { day ->
                MonthDayCell(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onClick = {
                        // 다른 월의 날짜를 클릭한 경우 애니메이션 후 선택
                        if (day.position != DayPosition.MonthDate) {
                            coroutineScope.launch {
                                state.animateScrollToMonth(YearMonth.from(day.date))
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

/**
 * 월 캘린더 헤더 (월/년도 + 화살표)
 */
@Composable
private fun MonthCalendarHeader(
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
private fun MonthWeekDaysHeader(
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
 * 월 캘린더 날짜 셀
 */
@Composable
private fun MonthDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .padding(4.dp)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFFE91E63), RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            fontSize = 16.sp,
            color = when {
                !isCurrentMonth -> Color.White.copy(alpha = 0.3f)
                else -> Color.White
            },
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
