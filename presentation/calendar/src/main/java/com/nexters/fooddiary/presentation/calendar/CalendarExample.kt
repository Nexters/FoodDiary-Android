package com.nexters.fooddiary.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/**
 * 캘린더 사용 예제
 *
 * 주단위와 월단위 캘린더를 탭으로 전환할 수 있는 예제입니다.
 */
@Composable
fun CalendarExample(
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // 월간 캘린더 상태
    val monthCalendarState = rememberMonthCalendarState(selectedDate = selectedDate)

    // 주간 캘린더 상태
    val weekCalendarState = rememberWeeklyCalendarState(selectedDate = selectedDate)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2E)) // 다크 배경색
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 탭 선택
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0xFF3A3A3C),
                contentColor = Color.White
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("주간") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("월간") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 캘린더 표시
            when (selectedTabIndex) {
                0 -> WeeklyCalendar(
                    calendarState = weekCalendarState,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.fillMaxWidth()
                )
                1 -> MonthlyCalendar(
                    calendarState = monthCalendarState,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarExamplePreview() {
    CalendarExample()
}
