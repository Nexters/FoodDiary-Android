package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.UpdateDiaryParam
import java.time.LocalDate
import java.time.YearMonth

interface DiaryRepository {
    suspend fun getDiary(date: LocalDate): DiaryDetail
    suspend fun getDiaryByMonth(yearMonth: YearMonth): Map<LocalDate, DiaryEntry>
    suspend fun getDiarySummary(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, List<String>>
    suspend fun getDiariesSummary(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, List<String>>
    suspend fun getDiary(id: Int): DiaryEntry
    suspend fun updateDiary(diaryId: Int, param: UpdateDiaryParam): DiaryEntry
    suspend fun deleteDiary(diaryId: Int)
}
