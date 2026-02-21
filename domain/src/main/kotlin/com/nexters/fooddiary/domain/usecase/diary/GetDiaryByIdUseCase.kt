package com.nexters.fooddiary.domain.usecase.diary

import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.repository.DiaryRepository
import javax.inject.Inject

class GetDiaryByIdUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(id: Int): DiaryEntry = diaryRepository.getDiary(id)
}