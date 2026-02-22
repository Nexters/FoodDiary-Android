package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.DiaryDetail
import java.time.LocalDate

interface DiaryRepository {
    suspend fun getDiary(date: LocalDate): DiaryDetail
    suspend fun getDiarySummary(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, List<String>>
}
