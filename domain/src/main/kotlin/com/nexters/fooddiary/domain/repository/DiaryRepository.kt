package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.DiaryDetail
import java.time.LocalDate
import java.time.YearMonth

interface DiaryRepository {
    suspend fun getDiary(date: LocalDate): DiaryDetail
    suspend fun getDiaryByMonth(yearMonth: YearMonth): Map<LocalDate, DiaryEntry>
}
