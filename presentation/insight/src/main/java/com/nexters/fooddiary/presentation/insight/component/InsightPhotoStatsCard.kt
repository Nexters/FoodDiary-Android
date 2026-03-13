package com.nexters.fooddiary.presentation.insight.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.component.BarChartCard
import com.nexters.fooddiary.core.ui.component.BarChartItem
import com.nexters.fooddiary.core.ui.component.withStaggeredAnimation
import com.nexters.fooddiary.core.ui.theme.Blue400
import com.nexters.fooddiary.core.ui.theme.Blue700
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Prim300
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.InsightPhotoStatsCardUiModel
import com.nexters.fooddiary.presentation.insight.R
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState
import java.time.YearMonth
import kotlin.math.abs

@Composable
internal fun InsightPhotoStatsCard(
    month: String,
    card: InsightPhotoStatsCardUiModel,
    modifier: Modifier = Modifier,
    startAnimation: Boolean = true,
) {
    val isDecrease = card.previousMonthCount > card.currentMonthCount
    val isEqual = card.previousMonthCount == card.currentMonthCount
    val labels = rememberMonthLabels(
        month = month,
        previousFallback = stringResource(id = R.string.insight_photo_stats_previous_month_fallback),
        currentFallback = stringResource(id = R.string.insight_photo_stats_current_month_fallback),
    )

    BarChartCard(
        title = when {
            isEqual -> stringResource(id = R.string.insight_photo_stats_title_equal)
            isDecrease -> stringResource(id = R.string.insight_photo_stats_title_decrease)
            else -> stringResource(id = R.string.insight_photo_stats_title_increase)
        },
        descriptionPrefix = stringResource(id = R.string.insight_photo_stats_description_prefix),
        highlightText = card.changeRate.toHighlightPercentText(),
        descriptionSuffix = when {
            isEqual -> stringResource(id = R.string.insight_photo_stats_description_suffix_equal)
            isDecrease -> stringResource(id = R.string.insight_photo_stats_description_suffix_decrease)
            else -> stringResource(id = R.string.insight_photo_stats_description_suffix_increase)
        },
        bars = listOf(
            BarChartItem(
                label = labels.previousMonthLabel,
                percentage = card.previousMonthCount.toChartPercentage(
                    other = card.currentMonthCount,
                ),
                valueText = card.previousMonthCount.toString(),
                topColor = Blue700,
                bottomColor = Blue400,
                animationDurationMillis = 520,
            ),
            BarChartItem(
                label = labels.currentMonthLabel,
                percentage = card.currentMonthCount.toChartPercentage(
                    other = card.previousMonthCount,
                ),
                valueText = card.currentMonthCount.toString(),
                topColor = PrimBase,
                bottomColor = Prim300,
                animationDurationMillis = 680,
            ),
        ).withStaggeredAnimation(delayStepMillis = 90),
        barSpacing = 60.dp,
        chartHeight = 182.dp,
        startAnimation = startAnimation,
        modifier = modifier,
    )
}

@Composable
private fun rememberMonthLabels(
    month: String,
    previousFallback: String,
    currentFallback: String,
): MonthLabels {
    val currentMonth = runCatching { YearMonth.parse(month) }.getOrNull()
    return if (currentMonth == null) {
        MonthLabels(
            previousMonthLabel = previousFallback,
            currentMonthLabel = currentFallback,
        )
    } else {
        MonthLabels(
            previousMonthLabel = "${currentMonth.minusMonths(1).monthValue}월",
            currentMonthLabel = "${currentMonth.monthValue}월",
        )
    }
}

private fun Int.toChartPercentage(other: Int): Float {
    val maxValue = maxOf(this, other, 1)
    return (toFloat() / maxValue.toFloat()) * 100f
}

private fun Double.toHighlightPercentText(): String {
    val absolute = abs(this)
    val roundedInteger = absolute.toInt().toDouble()
    return if (absolute == roundedInteger) {
        "${roundedInteger.toInt()}%"
    } else {
        String.format("%.1f%%", absolute)
    }
}

private data class MonthLabels(
    val previousMonthLabel: String,
    val currentMonthLabel: String,
)

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightPhotoStatsCardPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            InsightPhotoStatsCard(
                month = sampleInsightReadyState().month,
                card = sampleInsightReadyState().photoStatsCard,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
