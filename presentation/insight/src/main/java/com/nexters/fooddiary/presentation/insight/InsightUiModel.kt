package com.nexters.fooddiary.presentation.insight

import androidx.compose.ui.graphics.Color
import com.airbnb.mvrx.MavericksState
import com.nexters.fooddiary.core.ui.theme.PrimBase

data class InsightScreenState(
    val donutCard: InsightDonutCardUiModel? = null,
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
    val color: Color,
    val valueText: String? = null,
    val gradient: InsightSegmentGradientUiModel = InsightSegmentGradientUiModel(colors = listOf(color)),
)

data class InsightSegmentGradientUiModel(
    val colors: List<Color>,
    val start: InsightGradientPointUiModel = InsightGradientPointUiModel(0f, 0f),
    val end: InsightGradientPointUiModel = InsightGradientPointUiModel(1f, 1f),
)

data class InsightGradientPointUiModel(
    val xFraction: Float,
    val yFraction: Float,
)

data class InsightRankingBubbleCardUiModel(
    val topRegions: List<InsightRankingBubbleItemUiModel>,
)

data class InsightRankingBubbleItemUiModel(
    val rank: Int,
    val regionName: String,
    val visitCount: Int,
)

internal object InsightChartDefaults {
    val BlueSegmentColor = Color(0xFF6581C4)
    val BlueSegmentGradient = InsightSegmentGradientUiModel(
        colors = listOf(
            Color(0xFF8AA6E6),
            Color(0xFF415199),
        ),
        start = InsightGradientPointUiModel(xFraction = 0.5f, yFraction = 1f),
        end = InsightGradientPointUiModel(xFraction = 0.5f, yFraction = 0f),
    )
    val OrangeSegmentGradient = InsightSegmentGradientUiModel(
        colors = listOf(
            Color(0xFFFE670E),
            Color(0xFFFFB183),
        ),
        start = InsightGradientPointUiModel(xFraction = 0.5f, yFraction = 0f),
        end = InsightGradientPointUiModel(xFraction = 0.5f, yFraction = 1f),
    )
}

internal fun sampleInsightReadyState(): InsightScreenState = InsightScreenState(
    donutCard = sampleInsightDonutCard(),
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
                color = PrimBase,
                valueText = "28회",
                gradient = InsightChartDefaults.OrangeSegmentGradient,
            ),
            InsightDonutSegmentUiModel(
                label = "양식",
                value = 20f,
                color = InsightChartDefaults.BlueSegmentColor,
                valueText = "20회",
                gradient = InsightChartDefaults.BlueSegmentGradient,
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
