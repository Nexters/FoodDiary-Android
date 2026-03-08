package com.nexters.fooddiary.presentation.insight.rankingbubble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.InsightRankingBubbleCardUiModel
import com.nexters.fooddiary.presentation.insight.InsightRankingBubbleItemUiModel
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState

private val RankingBubbleCardHorizontalPadding = 16.dp
private val RankingBubbleCardVerticalPadding = 24.dp
private val RankingBubbleCardShape = RoundedCornerShape(16.dp)
private val RankingBubbleCardBackgroundColor = Color.White.copy(alpha = 0.02f)
private val RankingBubbleChartHeight = 320.dp
private val RankingBubbleChartWidth = 300.dp
private val RankingBubbleFirstSize = 160.dp
private val RankingBubbleSecondSize = 110.dp
private val RankingBubbleThirdSize = 80.dp
private val RankingBubbleCardContentSpacing = 16.dp
private val RankingBubbleHeadlineSectionSpacing = 6.dp
private val RankingBubbleChartWrapperBottomMargin = 24.dp
private val RankingBubbleChartWrapperMaxWidth = 296.dp
private val RankingBubbleChartWrapperMaxHeight = 240.dp

internal object InsightRankingBubbleDefaults {
    val FirstColor = Color(0xFFFE670E)
    val SecondColor = Color(0xFFFE670E).copy(alpha = 0.6f)
    val ThirdColor = Color(0xFFFE670E).copy(alpha = 0.2f)
}

private object InsightRankingBubblePlacementDefaults {
    val FirstOffset = BubbleOffset(x = (-58).dp, y = 34.dp)
    val SecondOffset = BubbleOffset(x = 80.dp, y = (-26).dp)
    val ThirdOffset = BubbleOffset(x = 70.dp, y = 82.dp)
}

private data class BubbleOffset(
    val x: Dp,
    val y: Dp,
)

@Composable
internal fun InsightRankingBubbleCard(
    card: InsightRankingBubbleCardUiModel,
    modifier: Modifier = Modifier,
) {
    val topRegion = card.topRegions.firstOrNull()

    Column(
        modifier = modifier
            .clip(RankingBubbleCardShape)
            .background(RankingBubbleCardBackgroundColor)
            .padding(
                horizontal = RankingBubbleCardHorizontalPadding,
                vertical = RankingBubbleCardVerticalPadding,
            ),
        verticalArrangement = Arrangement.spacedBy(RankingBubbleCardContentSpacing),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(RankingBubbleHeadlineSectionSpacing)) {
            Text(
                text = "이번 달 가장 많이 간 지역은",
                style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                color = Gray050,
            )
            topRegion?.let { region ->
                Text(
                    text = buildRankingBubbleHeadline(region),
                    style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Text(
                text = "밖에서 먹은 날이 더 많았어요.",
                style = AppTypography.p12.copy(fontSize = 12.sp),
                color = Gray050.copy(alpha = 0.6f),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = RankingBubbleChartWrapperMaxWidth)
                .heightIn(max = RankingBubbleChartWrapperMaxHeight)
                .align(Alignment.CenterHorizontally)
                .padding(
                    bottom = RankingBubbleChartWrapperBottomMargin,
                ),
            contentAlignment = Alignment.Center,
        ) {
            InsightRankingBubbleChart(
                topRegions = card.topRegions,
                modifier = Modifier.width(RankingBubbleChartWidth),
            )
        }
    }
}

@Composable
private fun InsightRankingBubbleChart(
    topRegions: List<InsightRankingBubbleItemUiModel>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(RankingBubbleChartHeight),
        contentAlignment = Alignment.Center,
    ) {
        topRegions.sortedBy { it.rank }.take(3).forEach { region ->
            InsightRankingBubble(
                region = region,
                modifier = Modifier
                    .offset(
                        x = region.bubbleOffsetX(),
                        y = region.bubbleOffsetY(),
                    ),
            )
        }
    }
}

@Composable
private fun InsightRankingBubble(
    region: InsightRankingBubbleItemUiModel,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(region.bubbleSize())
            .clip(CircleShape)
            .background(region.bubbleColor()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = region.regionName,
                style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                color = Gray050,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "(${region.visitCount}회)",
                style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                color = Gray050,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun buildRankingBubbleHeadline(
    topRegion: InsightRankingBubbleItemUiModel,
): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(color = Gray050)) {
        append("총 ${topRegion.visitCount}회 방문한 ")
    }
    withStyle(SpanStyle(color = InsightRankingBubbleDefaults.FirstColor)) {
        append(topRegion.regionName)
    }
}

internal fun InsightRankingBubbleItemUiModel.bubbleSize(): Dp = when (rank) {
    1 -> RankingBubbleFirstSize
    2 -> RankingBubbleSecondSize
    else -> RankingBubbleThirdSize
}

internal fun InsightRankingBubbleItemUiModel.bubbleColor(): Color = when (rank) {
    1 -> InsightRankingBubbleDefaults.FirstColor
    2 -> InsightRankingBubbleDefaults.SecondColor
    else -> InsightRankingBubbleDefaults.ThirdColor
}

private fun InsightRankingBubbleItemUiModel.bubbleOffsetX(): Dp = when (rank) {
    1 -> InsightRankingBubblePlacementDefaults.FirstOffset.x
    2 -> InsightRankingBubblePlacementDefaults.SecondOffset.x
    else -> InsightRankingBubblePlacementDefaults.ThirdOffset.x
}

private fun InsightRankingBubbleItemUiModel.bubbleOffsetY(): Dp = when (rank) {
    1 -> InsightRankingBubblePlacementDefaults.FirstOffset.y
    2 -> InsightRankingBubblePlacementDefaults.SecondOffset.y
    else -> InsightRankingBubblePlacementDefaults.ThirdOffset.y
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightRankingBubbleCardPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            sampleInsightReadyState().rankingBubbleCard?.let { card ->
                InsightRankingBubbleCard(
                    card = card,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
