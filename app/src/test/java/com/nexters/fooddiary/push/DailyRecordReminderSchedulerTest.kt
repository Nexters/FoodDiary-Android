package com.nexters.fooddiary.push

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class DailyRecordReminderSchedulerTest {
    private val zoneId = ZoneId.of("Asia/Seoul")

    @Test
    fun `next trigger is today 9pm when current time is before 9pm`() {
        val now = ZonedDateTime.of(2026, 4, 21, 20, 59, 0, 0, zoneId)
        val expected = ZonedDateTime.of(2026, 4, 21, 21, 0, 0, 0, zoneId)

        assertEquals(
            expected.toInstant().toEpochMilli(),
            DailyRecordReminderScheduler.nextTriggerAtMillis(now)
        )
    }

    @Test
    fun `next trigger is tomorrow 9pm when current time is 9pm`() {
        val now = ZonedDateTime.of(2026, 4, 21, 21, 0, 0, 0, zoneId)
        val expected = ZonedDateTime.of(2026, 4, 22, 21, 0, 0, 0, zoneId)

        assertEquals(
            expected.toInstant().toEpochMilli(),
            DailyRecordReminderScheduler.nextTriggerAtMillis(now)
        )
    }
}
