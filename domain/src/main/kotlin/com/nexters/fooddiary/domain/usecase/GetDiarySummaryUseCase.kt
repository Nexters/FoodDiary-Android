package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject

class GetDiarySummaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
) {
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, List<String>> {
        return diaryRepository.getDiarySummary(startDate = startDate, endDate = endDate)
    }
}
