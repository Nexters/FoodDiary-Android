package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.UserInsights

interface InsightRepository {
    suspend fun getInsights(): UserInsights
}
