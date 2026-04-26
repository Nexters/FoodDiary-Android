package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.ReviewPromptState

interface ReviewPromptRepository {
    suspend fun recordSuccessfulDiary()
    suspend fun getReviewPromptState(): ReviewPromptState
    suspend fun markInAppReviewRequested()
}
