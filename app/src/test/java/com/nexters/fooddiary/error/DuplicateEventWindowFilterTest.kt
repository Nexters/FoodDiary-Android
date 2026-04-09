package com.nexters.fooddiary.error

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateEventWindowFilterTest {

    @Test
    fun `첫 이벤트는 항상 통과한다`() {
        val filter = DuplicateEventWindowFilter(windowMillis = 2_000L)

        assertTrue(filter.shouldEmit(key = "Timeout:요청 시간이 초과되었습니다.", nowMillis = 1_000L))
    }

    @Test
    fun `동일 키가 윈도우 내 반복되면 차단한다`() {
        val filter = DuplicateEventWindowFilter(windowMillis = 2_000L)

        assertTrue(filter.shouldEmit(key = "Http:HTTP 500", nowMillis = 1_000L))
        assertFalse(filter.shouldEmit(key = "Http:HTTP 500", nowMillis = 2_500L))
    }

    @Test
    fun `동일 키라도 윈도우 이후면 통과한다`() {
        val filter = DuplicateEventWindowFilter(windowMillis = 2_000L)

        assertTrue(filter.shouldEmit(key = "Http:HTTP 500", nowMillis = 1_000L))
        assertTrue(filter.shouldEmit(key = "Http:HTTP 500", nowMillis = 3_100L))
    }

    @Test
    fun `다른 키는 윈도우 내에도 통과한다`() {
        val filter = DuplicateEventWindowFilter(windowMillis = 2_000L)

        assertTrue(filter.shouldEmit(key = "Http:HTTP 500", nowMillis = 1_000L))
        assertTrue(filter.shouldEmit(key = "NoConnection:네트워크에 연결할 수 없습니다.", nowMillis = 1_500L))
    }
}
