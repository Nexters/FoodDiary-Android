package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject

class GetDiaryByDateUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(date: LocalDate): DiaryDetail {
        return diaryRepository.getDiary(date)
    }
}
