package com.nexters.fooddiary.core.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val hourMinuteFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun String?.toLocalTimeText(zoneId: ZoneId = ZoneId.systemDefault()): String {
    if (this.isNullOrBlank()) return ""
    val instant = this.toInstantOrNull() ?: return ""
    return instant.atZone(zoneId).format(hourMinuteFormatter)
}

private fun String.toInstantOrNull(): Instant? {
    return runCatching { Instant.parse(this) }.getOrNull()
        ?: runCatching { OffsetDateTime.parse(this).toInstant() }.getOrNull()
        ?: runCatching { ZonedDateTime.parse(this).toInstant() }.getOrNull()
        ?: runCatching { LocalDateTime.parse(this).toInstant(ZoneOffset.UTC) }.getOrNull()
}
