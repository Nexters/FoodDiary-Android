package com.nexters.fooddiary.domain.usecase.diary

import com.nexters.fooddiary.domain.model.UpdateDiaryParam
import com.nexters.fooddiary.domain.repository.DiaryRepository
import javax.inject.Inject

class UpdateDiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(diaryId: Int, param: UpdateDiaryParam) =
        diaryRepository.updateDiary(diaryId, param)
}