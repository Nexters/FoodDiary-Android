package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.ReviewPromptState
import com.nexters.fooddiary.domain.repository.ReviewPromptRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewPromptUseCaseTest {

    @Test
    fun `성공 1회면 리뷰 요청 대상이 아니다`() = runTest {
        val repository = FakeReviewPromptRepository(
            state = ReviewPromptState(successfulRecordCount = 1)
        )
        val useCase = ShouldRequestInAppReviewUseCase(repository)

        assertFalse(useCase())
    }

    @Test
    fun `성공 2회면 리뷰 요청 대상이다`() = runTest {
        val repository = FakeReviewPromptRepository(
            state = ReviewPromptState(successfulRecordCount = 2)
        )
        val useCase = ShouldRequestInAppReviewUseCase(repository)

        assertTrue(useCase())
    }

    @Test
    fun `리뷰 요청 후에는 다시 요청하지 않는다`() = runTest {
        val repository = FakeReviewPromptRepository(
            state = ReviewPromptState(successfulRecordCount = 2)
        )
        val markUseCase = MarkInAppReviewRequestedUseCase(repository)
        val shouldRequestUseCase = ShouldRequestInAppReviewUseCase(repository)

        markUseCase()

        assertFalse(shouldRequestUseCase())
    }
}

private class FakeReviewPromptRepository(
    private var state: ReviewPromptState = ReviewPromptState()
) : ReviewPromptRepository {
    override suspend fun recordSuccessfulDiary() {
        state = state.copy(successfulRecordCount = state.successfulRecordCount + 1)
    }

    override suspend fun getReviewPromptState(): ReviewPromptState = state

    override suspend fun markInAppReviewRequested() {
        state = state.copy(hasRequestedReview = true)
    }
}
