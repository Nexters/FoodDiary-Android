package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.DiaryMapper
import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryByDateItemResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DiaryRepositoryImplTest {

    private val diaryApi: DiaryApi = mockk()
    private val diaryMapper: DiaryMapper = mockk(relaxed = true)

    @Test
    fun `요약_조회시_날짜_문자열을_LocalDate로_매핑한다`() = runTest {
        // Given
        val startDate = LocalDate.parse("2026-02-22")
        val endDate = LocalDate.parse("2026-02-28")
        val repository = DiaryRepositoryImpl(
            diaryApi = diaryApi,
            diaryMapper = diaryMapper,
            isDebug = true,
            useMockApi = true,
        )

        coEvery {
            diaryApi.getDiarySummary("2026-02-22", "2026-02-28", true)
        } returns mapOf(
            "2026-02-22" to DiarySummaryByDateItemResponse(
                photos = listOf("a", "b"),
            )
        )

        // When
        val result = repository.getDiarySummary(startDate = startDate, endDate = endDate)

        // Then
        assertEquals(listOf("a", "b"), result[LocalDate.parse("2026-02-22")])
        coVerify(exactly = 1) {
            diaryApi.getDiarySummary("2026-02-22", "2026-02-28", true)
        }
    }

    @Test
    fun `요약_조회시_잘못된_날짜는_건너뛰고_release에서는_test_mode_false로_호출한다`() = runTest {
        // Given
        val repository = DiaryRepositoryImpl(
            diaryApi = diaryApi,
            diaryMapper = diaryMapper,
            isDebug = false,
            useMockApi = false,
        )

        coEvery {
            diaryApi.getDiarySummary("2026-02-22", "2026-02-28", false)
        } returns mapOf(
            "invalid-date" to DiarySummaryByDateItemResponse(photos = listOf("x")),
            "2026-02-23" to DiarySummaryByDateItemResponse(photos = listOf("ok")),
        )

        // When
        val result = repository.getDiarySummary(
            startDate = LocalDate.parse("2026-02-22"),
            endDate = LocalDate.parse("2026-02-28"),
        )

        // Then
        assertEquals(1, result.size)
        assertTrue(result.containsKey(LocalDate.parse("2026-02-23")))
        assertEquals(listOf("ok"), result[LocalDate.parse("2026-02-23")])
        coVerify(exactly = 1) {
            diaryApi.getDiarySummary("2026-02-22", "2026-02-28", false)
        }
    }
}
