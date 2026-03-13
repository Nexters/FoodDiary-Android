package com.nexters.fooddiary.presentation.insight

import com.airbnb.mvrx.MavericksState

data class InsightScreenState(
    val hasInsights: Boolean = false,
    val month: String = "",
    val photoStatsCard: InsightPhotoStatsCardUiModel = InsightPhotoStatsCardUiModel(),
    val donutCard: InsightDonutCardUiModel = InsightDonutCardUiModel(),
    val weeklyStatsCard: InsightWeeklyStatsCardUiModel = InsightWeeklyStatsCardUiModel(),
    val mealTimeCard: InsightMealTimeCardUiModel = InsightMealTimeCardUiModel(),
    val tagStatsCard: InsightTagStatsCardUiModel = InsightTagStatsCardUiModel(),
    val rankingBubbleCard: InsightRankingBubbleCardUiModel = InsightRankingBubbleCardUiModel(),
) : MavericksState

data class InsightDonutCardUiModel(
    val previousTopCategory: String = "",
    val currentTopCategory: String = "",
    val segments: List<InsightDonutSegmentUiModel> = emptyList(),
)

data class InsightDonutSegmentUiModel(
    val label: String,
    val value: Float,
    val valueText: String? = null,
)

data class InsightMealTimeCardUiModel(
    val peakMealTime: String = "",
)

data class InsightRankingBubbleCardUiModel(
    val topRegions: List<InsightRankingBubbleItemUiModel> = emptyList(),
)

data class InsightRankingBubbleItemUiModel(
    val rank: Int,
    val regionName: String,
    val visitCount: Int,
)

data class InsightPhotoStatsCardUiModel(
    val currentMonthCount: Int = 0,
    val previousMonthCount: Int = 0,
    val changeRate: Double = 0.0,
)

data class InsightWeeklyStatsCardUiModel(
    val mostActiveWeek: Int = 0,
    val weeklyCounts: List<InsightWeeklyCountUiModel> = emptyList(),
)

data class InsightWeeklyCountUiModel(
    val week: Int,
    val count: Int,
)

data class InsightTagSummaryItemUiModel(
    val keyword: String,
    val count: Int,
)

data class InsightTagStatsCardUiModel(
    val tags: List<InsightTagSummaryItemUiModel> = emptyList(),
)
