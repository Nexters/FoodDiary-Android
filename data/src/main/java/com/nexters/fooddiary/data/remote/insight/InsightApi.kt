package com.nexters.fooddiary.data.remote.insight

import com.nexters.fooddiary.data.remote.insight.model.response.InsightsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface InsightApi {
    @GET("/me/insights")
    suspend fun getInsights(
        @Query("test_mode") testMode: Boolean = false,
    ): InsightsResponse
}
