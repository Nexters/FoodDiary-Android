package com.nexters.fooddiary.core.ui.calendar.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray500
import com.nexters.fooddiary.core.ui.theme.Gray700
import com.nexters.fooddiary.core.ui.theme.Gray800
import com.nexters.fooddiary.core.ui.theme.Gray900
import com.nexters.fooddiary.core.ui.theme.GrayBase
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Sd900

@Immutable
data class CalendarColors(
    // 헤더 색상
    val headerText: Color,
    val iconTint: Color,

    // 요일 헤더 색상
    val weekdayText: Color,

    // 날짜 셀 색상
    val dayText: Color,
    val dayTextDisabled: Color,  // 다른 월의 날짜
    val dayTextSelected: Color,  // 선택된 날짜의 텍스트

    // 선택 상태 색상
    val selectedBackground: Color,  // 선택된 날짜 배경 (주황색)
    val selectedInnerBox: Color,    // 선택된 날짜 내부 박스 (흰색)
    val unselectedInnerBox: Color,

    // 배경 색상
    val background: Color,
    val cellBackground: Color,  // 날짜 셀 배경
)

private val DarkCalendarColors = CalendarColors(
    // 헤더
    headerText = Gray050,
    iconTint = Gray050,

    // 요일
    weekdayText = Gray500,

    // 날짜
    dayText = Gray050,
    dayTextDisabled = Gray700,
    dayTextSelected = Gray050,

    // 선택 상태
    selectedBackground = PrimBase,
    selectedInnerBox = White.copy(0.2f),

    //TODO 디자인 문서에 343434로 되어있는데 선언안되어있어서 임의로 900 사용
    unselectedInnerBox = Gray900,

    // 배경
    background = Sd900,
    cellBackground = Gray800,
)

@Composable
fun calendarColors(): CalendarColors {
    return DarkCalendarColors
}
