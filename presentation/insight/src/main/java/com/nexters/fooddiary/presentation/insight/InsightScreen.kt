package com.nexters.fooddiary.presentation.insight

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.component.BarChartCard
import com.nexters.fooddiary.core.ui.component.BarChartItem
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.component.HighlightedSubjectBarChartCard
import com.nexters.fooddiary.core.ui.component.TasteKeywordSection
import com.nexters.fooddiary.core.ui.component.withStaggeredAnimation
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Blue400
import com.nexters.fooddiary.core.ui.theme.Blue700
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Prim300
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.donut.InsightDonutCard
import com.nexters.fooddiary.presentation.insight.component.InsightMealTimeCard
import com.nexters.fooddiary.core.ui.R as coreR
import com.nexters.fooddiary.presentation.insight.rankingbubble.InsightRankingBubbleCard
import java.time.YearMonth
import kotlin.math.abs

@Composable
fun InsightScreen(
    modifier: Modifier = Modifier,
    onNavigateToMyPage: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: InsightViewModel = mavericksViewModel(),
) {
    val uiState by viewModel.collectAsStateWithLifecycle()

    InsightScreen(
        uiState = uiState,
        modifier = modifier,
        onNavigateToMyPage = onNavigateToMyPage,
        onBack = onBack,
    )
}

@Composable
internal fun InsightScreen(
    modifier: Modifier = Modifier,
    uiState: InsightScreenState,
    onNavigateToMyPage: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    ) {
        Header(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 20.dp, top = 38.dp, end = 20.dp),
            leftIconResId = coreR.drawable.ic_app_icon,
            leftIconColorFilter = null,
            onClickMyPage = onNavigateToMyPage,
        )

        if (uiState.hasInsights) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 102.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PhotoStatsInsightCard(
                    month = uiState.month,
                    card = uiState.photoStatsCard,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (uiState.tagStatsCard.tags.isNotEmpty()) {
                    TasteKeywordSection(
                        title = stringResource(id = R.string.insight_tag_stats_title),
                        keywords = uiState.tagStatsCard.tags.toDisplayKeywords(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (uiState.donutCard.segments.isNotEmpty()) {
                    InsightDonutCard(
                        card = uiState.donutCard,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (uiState.weeklyStatsCard.weeklyCounts.isNotEmpty()) {
                    WeeklyStatsInsightCard(
                        card = uiState.weeklyStatsCard,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (uiState.mealTimeCard.peakMealTime.isNotBlank()) {
                    InsightMealTimeCard(
                        highlightText = uiState.mealTimeCard.peakMealTime,
                        descriptionText = stringResource(id = R.string.insight_meal_time_description),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (uiState.rankingBubbleCard.topRegions.isNotEmpty()) {
                    InsightRankingBubbleCard(
                        card = uiState.rankingBubbleCard,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_ready_insight),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(42.dp))
                Text(
                    text = stringResource(id = R.string.insight_ready_message),
                    style = AppTypography.p15,
                    color = Gray050,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun PhotoStatsInsightCard(
    month: String?,
    card: InsightPhotoStatsCardUiModel,
    modifier: Modifier = Modifier,
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
        modifier = modifier,
    )
}

@Composable
private fun WeeklyStatsInsightCard(
    card: InsightWeeklyStatsCardUiModel,
    modifier: Modifier = Modifier,
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
        modifier = modifier,
    )
}

@Composable
private fun rememberMonthLabels(
    month: String?,
    previousFallback: String,
    currentFallback: String,
): MonthLabels {
    val currentMonth = runCatching { month?.let(YearMonth::parse) }.getOrNull()
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

private fun List<InsightTagSummaryItemUiModel>.toDisplayKeywords(): List<String> {
    return map { tag ->
        val trimmed = tag.keyword.trim()
        if (trimmed.startsWith("#")) trimmed else "#$trimmed"
    }
}

@Preview
@Composable
private fun InsightScreenPreview() {
    FoodDiaryTheme {
        InsightScreen(uiState = InsightScreenState())
    }
}

@Preview(heightDp = 2000)
@Composable
private fun InsightScreenReadyPreview() {
    FoodDiaryTheme {
        InsightScreen(uiState = sampleInsightReadyState())
    }
}
