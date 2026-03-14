package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.toDomain
import com.nexters.fooddiary.data.remote.insight.InsightApi
import com.nexters.fooddiary.domain.model.UserInsights
import com.nexters.fooddiary.domain.repository.InsightRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class InsightRepositoryImpl @Inject constructor(
    private val insightApi: InsightApi,
    @Named("isDebug") private val isDebug: Boolean,
) : InsightRepository {

    override suspend fun getInsights(): UserInsights {
        return insightApi.getInsights(testMode = isDebug).toDomain()
    }
}
