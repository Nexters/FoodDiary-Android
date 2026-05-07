package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.local.ReviewPromptStore
import com.nexters.fooddiary.domain.model.ReviewPromptState
import com.nexters.fooddiary.domain.repository.ReviewPromptRepository
import javax.inject.Inject

class ReviewPromptRepositoryImpl @Inject constructor(
    private val reviewPromptStore: ReviewPromptStore
) : ReviewPromptRepository {
    override suspend fun recordSuccessfulDiary() {
        reviewPromptStore.incrementSuccessfulRecordCount()
    }

    override suspend fun getReviewPromptState(): ReviewPromptState {
        return reviewPromptStore.getState()
    }

    override suspend fun markInAppReviewRequested() {
        reviewPromptStore.markInAppReviewRequested()
    }
}
