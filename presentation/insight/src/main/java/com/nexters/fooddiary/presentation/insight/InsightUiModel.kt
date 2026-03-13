package com.nexters.fooddiary.presentation.insight

import com.airbnb.mvrx.MavericksState

data class InsightScreenState(
    val month: String? = null,
    val photoStatsCard: InsightPhotoStatsCardUiModel? = null,
    val donutCard: InsightDonutCardUiModel? = null,
    val weeklyStatsCard: InsightWeeklyStatsCardUiModel? = null,
    val mealTimeCard: InsightMealTimeCardUiModel? = null,
    val tagStatsCard: InsightTagStatsCardUiModel? = null,
    val rankingBubbleCard: InsightRankingBubbleCardUiModel? = null,
) : MavericksState

data class InsightDonutCardUiModel(
    val previousTopCategory: String,
    val currentTopCategory: String,
    val segments: List<InsightDonutSegmentUiModel>,
)

data class InsightDonutSegmentUiModel(
    val label: String,
    val value: Float,
    val valueText: String? = null,
)

data class InsightMealTimeCardUiModel(
    val peakMealTime: String,
)

data class InsightRankingBubbleCardUiModel(
    val topRegions: List<InsightRankingBubbleItemUiModel>,
)

data class InsightRankingBubbleItemUiModel(
    val rank: Int,
    val regionName: String,
    val visitCount: Int,
)

data class InsightPhotoStatsCardUiModel(
    val currentMonthCount: Int,
    val previousMonthCount: Int,
    val changeRate: Double,
)

data class InsightWeeklyStatsCardUiModel(
    val mostActiveWeek: Int,
    val weeklyCounts: List<InsightWeeklyCountUiModel>,
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
    val tags: List<InsightTagSummaryItemUiModel>,
)
