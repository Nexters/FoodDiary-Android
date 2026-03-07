package com.nexters.fooddiary.core.common

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private val hourMinuteFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun String?.toLocalTimeText(): String {
    if (this.isNullOrBlank()) return ""
    return runCatching {
        LocalDateTime.parse(this).format(hourMinuteFormatter)
    }.getOrElse {
        runCatching {
            OffsetDateTime.parse(this).format(hourMinuteFormatter)
        }.getOrDefault("")
    }
}
