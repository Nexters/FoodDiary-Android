package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.Diary
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class GetDiaryByMonthUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(yearMonth: YearMonth): Map<LocalDate, Diary> {
        return diaryRepository.getDiaryByMonth(yearMonth)
    }
}
