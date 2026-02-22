package com.nexters.fooddiary.presentation.detail.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val koreanDayOfWeekFormatter = DateTimeFormatter.ofPattern("E", Locale.KOREAN)

internal fun LocalDate.toDailyHeaderText(): String {
    val dayOfWeek = format(koreanDayOfWeekFormatter)

    return "${year}년 ${monthValue}월 ${dayOfMonth}일 ($dayOfWeek)"
}
