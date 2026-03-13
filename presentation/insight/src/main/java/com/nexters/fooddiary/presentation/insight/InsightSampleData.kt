package com.nexters.fooddiary.presentation.insight

internal fun sampleInsightReadyState(): InsightScreenState = InsightScreenState(
    hasInsights = true,
    month = "2024-01",
    photoStatsCard = InsightPhotoStatsCardUiModel(
        currentMonthCount = 45,
        previousMonthCount = 30,
        changeRate = 50.0,
    ),
    donutCard = sampleInsightDonutCard(),
    weeklyStatsCard = InsightWeeklyStatsCardUiModel(
        mostActiveWeek = 2,
        weeklyCounts = listOf(
            InsightWeeklyCountUiModel(week = 1, count = 3),
            InsightWeeklyCountUiModel(week = 2, count = 8),
            InsightWeeklyCountUiModel(week = 3, count = 5),
            InsightWeeklyCountUiModel(week = 4, count = 6),
        ),
    ),
    mealTimeCard = InsightMealTimeCardUiModel(
        peakMealTime = "19:00",
    ),
    tagStatsCard = InsightTagStatsCardUiModel(
        tags = listOf(
            InsightTagSummaryItemUiModel(keyword = "칼국수", count = 4),
            InsightTagSummaryItemUiModel(keyword = "라멘", count = 3),
        ),
    ),
    rankingBubbleCard = sampleInsightRankingBubbleCard(),
)

internal fun sampleInsightDonutCard(): InsightDonutCardUiModel =
    InsightDonutCardUiModel(
        previousTopCategory = "양식",
        currentTopCategory = "한식",
        segments = listOf(
            InsightDonutSegmentUiModel(
                label = "한식",
                value = 28f,
                valueText = "28회",
            ),
            InsightDonutSegmentUiModel(
                label = "양식",
                value = 20f,
                valueText = "20회",
            ),
        ),
    )

internal fun sampleInsightRankingBubbleCard(): InsightRankingBubbleCardUiModel =
    InsightRankingBubbleCardUiModel(
        topRegions = listOf(
            InsightRankingBubbleItemUiModel(
                rank = 1,
                regionName = "합정동",
                visitCount = 99,
            ),
            InsightRankingBubbleItemUiModel(
                rank = 2,
                regionName = "성수동",
                visitCount = 40,
            ),
            InsightRankingBubbleItemUiModel(
                rank = 3,
                regionName = "연남동",
                visitCount = 30,
            ),
        ),
    )
