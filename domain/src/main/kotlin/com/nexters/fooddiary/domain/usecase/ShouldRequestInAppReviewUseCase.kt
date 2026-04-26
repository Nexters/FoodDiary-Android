package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.ReviewPromptRepository
import javax.inject.Inject

class ShouldRequestInAppReviewUseCase @Inject constructor(
    private val reviewPromptRepository: ReviewPromptRepository
) {
    suspend operator fun invoke(): Boolean {
        val state = reviewPromptRepository.getReviewPromptState()
        return state.successfulRecordCount >= REVIEW_PROMPT_THRESHOLD && !state.hasRequestedReview
    }
}

private const val REVIEW_PROMPT_THRESHOLD = 2
