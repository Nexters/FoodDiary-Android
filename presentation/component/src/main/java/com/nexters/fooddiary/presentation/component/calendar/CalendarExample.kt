package com.nexters.fooddiary.presentation.component.calendar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

/**
 * 캘린더 사용 예제
 * 
 * 주단위와 월단위 캘린더를 탭으로 전환할 수 있는 예제입니다.
 */
@Composable
fun CalendarExample(
    modifier: Modifier = Modifier,
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
    onMonthChanged: (YearMonth) -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // Mock 데이터 (실제로는 외부에서 전달받음)
    val mockPhotoCount = remember {
        mapOf(
            LocalDate.now() to 3,
            LocalDate.now().minusDays(1) to 5,
            LocalDate.now().minusDays(3) to 2,
            LocalDate.now().plusDays(2) to 1
        )
    }
    
    val actualPhotoCount = photoCountByDate.ifEmpty { mockPhotoCount }
    
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
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.fillMaxWidth()
                )
                1 -> MonthlyCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    photoCountByDate = actualPhotoCount,
                    onMonthChanged = { yearMonth ->
                        Log.d("CalendarExample", "Month changed to: $yearMonth")
                        onMonthChanged(yearMonth)
                    },
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
