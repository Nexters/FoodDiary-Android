package com.nexters.fooddiary.presentation.home

import com.nexters.fooddiary.core.ui.food.FoodImageState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HomeSummaryLogicTest {

    @Test
    fun `weekStartOf는_기본값으로_일요일을_반환한다`() {
        val date = LocalDate.parse("2026-02-24") // Tuesday

        val weekStart = weekStartOf(date)

        assertEquals(LocalDate.parse("2026-02-22"), weekStart)
    }

    @Test
    fun `이미_로드된_주차이면_재조회하지_않는다`() {
        val selectedDate = LocalDate.parse("2026-02-26")
        val loadedWeekStartDate = LocalDate.parse("2026-02-22")

        assertFalse(shouldLoadWeek(selectedDate, loadedWeekStartDate))
    }

    @Test
    fun `다른_주차로_이동하면_재조회한다`() {
        val selectedDate = LocalDate.parse("2026-03-01")
        val loadedWeekStartDate = LocalDate.parse("2026-02-22")

        assertTrue(shouldLoadWeek(selectedDate, loadedWeekStartDate))
    }

    @Test
    fun `선택일_이미지_리스트를_정확히_반환한다`() {
        val selectedDate = LocalDate.parse("2026-02-22")
        val map = mapOf(
            LocalDate.parse("2026-02-22") to listOf("a", "b"),
            LocalDate.parse("2026-02-23") to listOf("c"),
        )

        val result = selectedDateImageUrls(
            weeklyPhotosByDate = map,
            selectedDate = selectedDate,
            selectedDateImageStatesByUrl = emptyMap(),
        )

        assertEquals(listOf("a", "b"), result)
    }

    @Test
    fun `summary 이미지가 없어도 detail 이미지로 fallback 한다`() {
        val selectedDate = LocalDate.parse("2026-02-22")

        val result = selectedDateImageUrls(
            weeklyPhotosByDate = emptyMap(),
            selectedDate = selectedDate,
            selectedDateImageStatesByUrl = linkedMapOf(
                "processing-a" to FoodImageState.Processing,
                "processing-b" to FoodImageState.Processing,
            ),
        )

        assertEquals(listOf("processing-a", "processing-b"), result)
    }
}
