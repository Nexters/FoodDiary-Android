package com.nexters.fooddiary.presentation.insight

import com.nexters.fooddiary.domain.model.InsightCategoryCounts
import com.nexters.fooddiary.domain.model.InsightLocationStat
import com.nexters.fooddiary.domain.model.UserInsights

internal fun UserInsights.toInsightScreenState(): InsightScreenState {
    return InsightScreenState(
        hasInsights = true,
        month = month,
        photoStatsCard = InsightPhotoStatsCardUiModel(
            currentMonthCount = photoStats.currentMonthCount,
            previousMonthCount = photoStats.previousMonthCount,
            changeRate = photoStats.changeRate,
        ),
        donutCard = toDonutCardUiModel(),
        weeklyStatsCard = InsightWeeklyStatsCardUiModel(
            mostActiveWeek = weeklyStats.mostActiveWeek,
            weeklyCounts = weeklyStats.weeklyCounts.map { weeklyCount ->
                InsightWeeklyCountUiModel(
                    week = weeklyCount.week,
                    count = weeklyCount.count,
                )
            },
        ),
        mealTimeCard = InsightMealTimeCardUiModel(
            peakMealTime = diaryTimeStats.mostActiveTime,
        ),
        tagStatsCard = InsightTagStatsCardUiModel(
            tags = tagStats.map { tag ->
                InsightTagSummaryItemUiModel(
                    keyword = tag.keyword,
                    count = tag.count,
                )
            },
        ),
        rankingBubbleCard = locationStats.toRankingBubbleCardUiModel(),
    )
}

private fun UserInsights.toDonutCardUiModel(): InsightDonutCardUiModel {
    val segments = categoryStats.currentMonthCounts.toDonutSegments(
        preferredTopCategory = categoryStats.currentMonth.topCategory,
    )
    if (segments.isEmpty()) return InsightDonutCardUiModel()

    return InsightDonutCardUiModel(
        previousTopCategory = categoryStats.previousMonth.topCategory.toInsightCategoryLabel(),
        currentTopCategory = categoryStats.currentMonth.topCategory.toInsightCategoryLabel(),
        segments = segments,
    )
}

private fun InsightCategoryCounts.toDonutSegments(
    preferredTopCategory: String,
): List<InsightDonutSegmentUiModel> {
    return listOf(
        CategorySegmentSource(InsightCategoryType.KOREAN, korean),
        CategorySegmentSource(InsightCategoryType.JAPANESE, japanese),
        CategorySegmentSource(InsightCategoryType.CHINESE, chinese),
        CategorySegmentSource(InsightCategoryType.WESTERN, western),
        CategorySegmentSource(InsightCategoryType.HOME_COOKED, homeCooked),
        CategorySegmentSource(InsightCategoryType.ETC, etc),
    ).filter { it.count > 0 }
        .sortedWith(
            compareByDescending<CategorySegmentSource> { it.count }
                .thenByDescending { it.categoryType.raw == preferredTopCategory }
        )
        .take(2)
        .map { source ->
            InsightDonutSegmentUiModel(
                label = source.categoryType.label,
                value = source.count.toFloat(),
                valueText = "${source.count}회",
            )
        }
}

private fun List<InsightLocationStat>.toRankingBubbleCardUiModel(): InsightRankingBubbleCardUiModel {
    val topRegions = sortedByDescending { it.count }
        .take(3)
        .mapIndexed { index, location ->
            InsightRankingBubbleItemUiModel(
                rank = index + 1,
                regionName = location.dong,
                visitCount = location.count,
            )
        }

    return InsightRankingBubbleCardUiModel(topRegions = topRegions)
}

private data class CategorySegmentSource(
    val categoryType: InsightCategoryType,
    val count: Int,
)
