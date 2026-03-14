package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.UserInsights
import com.nexters.fooddiary.domain.repository.InsightRepository
import javax.inject.Inject

class GetUserInsightsUseCase @Inject constructor(
    private val insightRepository: InsightRepository,
) {
    suspend operator fun invoke(): UserInsights {
        return insightRepository.getInsights()
    }
}
