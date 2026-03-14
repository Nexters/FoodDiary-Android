package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.DiaryMapper
import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.data.remote.diary.model.DiaryAnalysisStatusResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryDetailResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryMealTypeResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryPhoto
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryPhoto
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryResponse
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryByDateItemResponse
import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.MealType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

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
        )

        coEvery {
            diaryApi.getDiarySummary("2026-02-22", "2026-02-28", true)
        } returns mapOf(
            "2026-02-22" to DiarySummaryByDateItemResponse(
                photos = listOf(
                    DiarySummaryPhoto(url = "a"),
                    DiarySummaryPhoto(url = "b"),
                ),
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
        )

        coEvery {
            diaryApi.getDiarySummary("2026-02-22", "2026-02-28", false)
        } returns mapOf(
            "invalid-date" to DiarySummaryByDateItemResponse(photos = listOf(DiarySummaryPhoto(url = "x"))),
            "2026-02-23" to DiarySummaryByDateItemResponse(photos = listOf(DiarySummaryPhoto(url = "ok"))),
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

    @Test
    fun `일별_조회시_datetime_diary_date를_요청_날짜로_정상_필터링한다`() = runTest {
        // Given
        val repository = DiaryRepositoryImpl(
            diaryApi = diaryApi,
            diaryMapper = DiaryMapper(),
            isDebug = true,
        )
        val requestedDate = LocalDate.parse("2026-02-25")
        coEvery {
            diaryApi.getDiary("2026-02-25", "2026-02-25", true)
        } returns DiaryDetailResponse(
            diaries = listOf(
                diarySummary(
                    diaryId = 125L,
                    diaryDate = "2026-02-25T00:00:00",
                    timeType = DiaryMealTypeResponse.LUNCH,
                    analysisStatus = DiaryAnalysisStatusResponse.DONE,
                ),
                diarySummary(
                    diaryId = 80L,
                    diaryDate = "2026-02-25T01:33:16",
                    timeType = DiaryMealTypeResponse.SNACK,
                    analysisStatus = DiaryAnalysisStatusResponse.FAILED,
                ),
                diarySummary(
                    diaryId = 79L,
                    diaryDate = "2026-02-24T23:59:59",
                    timeType = DiaryMealTypeResponse.DINNER,
                    analysisStatus = DiaryAnalysisStatusResponse.DONE,
                ),
            )
        )

        // When
        val result = repository.getDiary(requestedDate)

        // Then
        assertEquals(2, result.diaries.size)
        assertTrue(result.diaries.any { it.mealType == MealType.LUNCH })
        assertTrue(result.diaries.any { it.mealType == MealType.SNACK })
        assertTrue(result.diaries.any { it.analysisStatus == AnalysisStatus.FAILED })
    }

    @Test
    fun `월별_조회시_datetime_diary_date를_LocalDate로_그룹핑한다`() = runTest {
        // Given
        val repository = DiaryRepositoryImpl(
            diaryApi = diaryApi,
            diaryMapper = DiaryMapper(),
            isDebug = true,
        )
        val yearMonth = YearMonth.parse("2026-02")
        coEvery {
            diaryApi.getDiary("2026-02-01", "2026-02-28", true)
        } returns DiaryDetailResponse(
            diaries = listOf(
                diarySummary(
                    diaryId = 125L,
                    diaryDate = "2026-02-25T00:00:00",
                    timeType = DiaryMealTypeResponse.LUNCH,
                    analysisStatus = DiaryAnalysisStatusResponse.DONE,
                ),
                diarySummary(
                    diaryId = 81L,
                    diaryDate = "2026-02-26T18:00:00",
                    timeType = DiaryMealTypeResponse.DINNER,
                    analysisStatus = DiaryAnalysisStatusResponse.PROCESSING,
                ),
            )
        )

        // When
        val result = repository.getDiaryByMonth(yearMonth)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.containsKey(LocalDate.parse("2026-02-25")))
        assertTrue(result.containsKey(LocalDate.parse("2026-02-26")))
    }

    @Test
    fun `일별_조회시_address_name이_있으면_location으로_우선_매핑한다`() = runTest {
        // Given
        val repository = DiaryRepositoryImpl(
            diaryApi = diaryApi,
            diaryMapper = DiaryMapper(),
            isDebug = true,
        )
        val requestedDate = LocalDate.parse("2026-02-25")
        coEvery {
            diaryApi.getDiary("2026-02-25", "2026-02-25", true)
        } returns DiaryDetailResponse(
            diaries = listOf(
                diarySummary(
                    diaryId = 125L,
                    diaryDate = "2026-02-25T00:00:00",
                    timeType = DiaryMealTypeResponse.LUNCH,
                    analysisStatus = DiaryAnalysisStatusResponse.DONE,
                    addressName = "서울시 강남구 테헤란로 123",
                    roadAddress = "서울시 강남구 테헤란로 999",
                ),
            )
        )

        // When
        val result = repository.getDiary(requestedDate)

        // Then
        assertEquals("서울시 강남구 테헤란로 123", result.diaries.first().location)
    }

    private fun diarySummary(
        diaryId: Long,
        diaryDate: String,
        timeType: DiaryMealTypeResponse,
        analysisStatus: DiaryAnalysisStatusResponse,
        addressName: String? = null,
        roadAddress: String? = null,
    ): DiarySummaryResponse {
        return DiarySummaryResponse(
            diaryId = diaryId,
            diaryDate = diaryDate,
            timeType = timeType,
            analysisStatus = analysisStatus,
            restaurantName = "식당",
            restaurantUrl = null,
            addressName = addressName,
            category = "한식",
            note = null,
            roadAddress = roadAddress,
            tags = emptyList(),
            coverPhotoUrl = "https://example.com/$diaryId.jpg",
            userId = "user-$diaryId",
            coverPhotoId = diaryId * 10,
            createdAt = "2026-02-25T12:00:00",
            updatedAt = "2026-02-25T12:00:00",
            photoCount = 1,
            photos = listOf(
                DiaryPhoto(
                    photoId = diaryId * 10,
                    imageUrl = "https://example.com/$diaryId.jpg",
                )
            ),
        )
    }
}
