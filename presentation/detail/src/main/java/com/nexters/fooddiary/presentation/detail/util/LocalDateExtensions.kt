package com.nexters.fooddiary.presentation.detail.util

import java.time.DayOfWeek
import java.time.LocalDate

internal fun LocalDate.toDailyHeaderText(): String {
    val dayOfWeek = when (dayOfWeek) {
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
        DayOfWeek.SUNDAY -> "일"
    }

    return "${year}년 ${monthValue}월 ${dayOfMonth}일 ($dayOfWeek)"
}
