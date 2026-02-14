package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.Diary
import java.time.LocalDate
import java.time.YearMonth

interface DiaryRepository {
    suspend fun getDiaryByMonth(yearMonth: YearMonth): Map<LocalDate, Diary>
}
