package com.nexters.fooddiary.domain.usecase.diary

import com.nexters.fooddiary.domain.repository.DiaryRepository
import javax.inject.Inject

class DeleteDiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(diaryId: Int) = diaryRepository.deleteDiary(diaryId)
}
