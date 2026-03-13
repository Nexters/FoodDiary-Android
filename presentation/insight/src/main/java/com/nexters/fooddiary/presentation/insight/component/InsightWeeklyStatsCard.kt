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
import com.nexters.fooddiary.core.ui.component.BarChartItem
import com.nexters.fooddiary.core.ui.component.HighlightedSubjectBarChartCard
import com.nexters.fooddiary.core.ui.component.withStaggeredAnimation
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Prim300
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.InsightWeeklyCountUiModel
import com.nexters.fooddiary.presentation.insight.InsightWeeklyStatsCardUiModel
import com.nexters.fooddiary.presentation.insight.R
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState

@Composable
internal fun InsightWeeklyStatsCard(
    card: InsightWeeklyStatsCardUiModel,
    modifier: Modifier = Modifier,
    startAnimation: Boolean = true,
) {
    val maxCount = card.weeklyCounts.maxOfOrNull(InsightWeeklyCountUiModel::count) ?: 0

    HighlightedSubjectBarChartCard(
        title = stringResource(id = R.string.insight_weekly_stats_title),
        description = stringResource(id = R.string.insight_weekly_stats_description),
        highlightPrefixText = stringResource(id = R.string.insight_weekly_stats_highlight_prefix),
        highlightedText = stringResource(
            id = R.string.insight_weekly_stats_label,
            card.mostActiveWeek,
        ),
        bars = card.weeklyCounts.mapIndexed { index, weeklyCount ->
            BarChartItem(
                label = stringResource(
                    id = R.string.insight_weekly_stats_label,
                    weeklyCount.week,
                ),
                percentage = weeklyCount.count.toChartPercentage(other = maxCount),
                valueText = weeklyCount.count.toString(),
                topColor = PrimBase,
                bottomColor = Prim300,
                animationDurationMillis = 520 + (index * 60),
            )
        }.withStaggeredAnimation(delayStepMillis = 70),
        barSpacing = 10.dp,
        startAnimation = startAnimation,
        modifier = modifier,
    )
}

private fun Int.toChartPercentage(other: Int): Float {
    val maxValue = maxOf(this, other, 1)
    return (toFloat() / maxValue.toFloat()) * 100f
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightWeeklyStatsCardPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            InsightWeeklyStatsCard(
                card = sampleInsightReadyState().weeklyStatsCard,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
