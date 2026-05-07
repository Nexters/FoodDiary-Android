package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.ReviewPromptRepository
import javax.inject.Inject

class RecordReviewPromptSuccessUseCase @Inject constructor(
    private val reviewPromptRepository: ReviewPromptRepository
) {
    suspend operator fun invoke() {
        reviewPromptRepository.recordSuccessfulDiary()
    }
}
