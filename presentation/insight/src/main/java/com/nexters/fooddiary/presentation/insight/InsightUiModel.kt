package com.nexters.fooddiary.presentation.insight

import androidx.compose.ui.graphics.Color
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.PrimBase

data class InsightScreenState(
    val isReady: Boolean = false,
    val highlightCard: InsightHighlightCardUiModel? = null,
)

data class InsightHighlightCardUiModel(
    val title: String,
    val headlineParts: List<InsightTextPartUiModel>,
    val caption: String,
    val segments: List<InsightDonutSegmentUiModel>,
)

data class InsightTextPartUiModel(
    val text: String,
    val color: Color,
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
    isReady = true,
    highlightCard = InsightHighlightCardUiModel(
        title = "왕좌가 바뀌었어요",
        headlineParts = listOf(
            InsightTextPartUiModel(text = "양식", color = InsightChartDefaults.BlueSegmentColor),
            InsightTextPartUiModel(text = " 대신 ", color = Gray050),
            InsightTextPartUiModel(text = "한식", color = PrimBase),
            InsightTextPartUiModel(text = " 이 1등이에요.", color = Gray050),
        ),
        caption = "추운 겨울, 국물음식이 더 끌리죠.",
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
    ),
)