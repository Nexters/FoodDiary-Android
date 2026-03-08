package com.nexters.fooddiary.core.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.horizontalGridLinesFromBottom
import kotlinx.coroutines.delay

private val DefaultChartHeight = 182.dp
private val DefaultCompareBarSpacing = 60.dp
private val DefaultFrequentBarSpacing = 10.dp
private const val ChartLineCount = 5
private val BarWidth = 42.dp
private val BarTopRadius = 2.dp
private val ValueTopPadding = 8.dp
private val BarLabelSpacing = 10.dp
private val BarLabelLineHeight = 14.sp
private val MinChartAreaHeight = 120.dp
private val BarLabelTextStyle = AppTypography.p12.copy(
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = (-0.15).sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
)
private val CardTitleTextStyle = AppTypography.p15.copy(
    fontWeight = FontWeight.SemiBold,
    lineHeight = 21.sp,
    letterSpacing = (-0.225).sp,
)

@Immutable
data class BarItem(
    val label: String,
    val ratio: Float,
    val valueText: String,
    val topColor: Color,
    val bottomColor: Color,
    val animationDurationMillis: Int = 900,
    val animationDelayMillis: Int = 0,
)

fun List<BarItem>.withStaggeredAnimation(
    delayStepMillis: Int = 100,
    startDelayMillis: Int = 0,
): List<BarItem> {
    if (isEmpty() || (delayStepMillis == 0 && startDelayMillis == 0)) return this
    return mapIndexed { index, item ->
    item.copy(animationDelayMillis = startDelayMillis + (index * delayStepMillis))
}
}

@Composable
fun BarChartCard(
    title: String,
    descriptionPrefix: String,
    highlightText: String,
    descriptionSuffix: String,
    bars: List<BarItem>,
    modifier: Modifier = Modifier,
    barSpacing: Dp = DefaultCompareBarSpacing,
    chartHeight: Dp = DefaultChartHeight,
) {
    val titleStyle = CardTitleTextStyle
    val highlightStyle = titleStyle.copy(color = PrimBase)

    BaseBarChartCard(
        bars = bars,
        modifier = modifier,
        barSpacing = barSpacing,
        chartHeight = chartHeight,
        headerContent = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = titleStyle,
                    color = Gray050,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Gray200, fontSize = 10.sp)) {
                            append(descriptionPrefix)
                        }
                        withStyle(
                            SpanStyle(
                                color = highlightStyle.color,
                                fontSize = highlightStyle.fontSize,
                                fontWeight = highlightStyle.fontWeight,
                                letterSpacing = highlightStyle.letterSpacing,
                            )
                        ) {
                            append(highlightText)
                        }
                        withStyle(SpanStyle(color = Gray200, fontSize = 10.sp)) {
                            append(descriptionSuffix)
                        }
                    },
                    style = BarLabelTextStyle,
                )
            }
        },
    )
}

@Composable
fun InsightFrequentFoodBarChartCard(
    title: String,
    description: String,
    highlightPrefixText: String,
    highlightFoodName: String,
    bars: List<BarItem>,
    modifier: Modifier = Modifier,
    barSpacing: Dp = DefaultFrequentBarSpacing,
    chartHeight: Dp = DefaultChartHeight,
) {
    val titleStyle = CardTitleTextStyle

    BaseBarChartCard(
        bars = bars,
        modifier = modifier,
        barSpacing = barSpacing,
        chartHeight = chartHeight,
        headerContent = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = title,
                        style = titleStyle,
                        color = Gray050,
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Gray050)) {
                                append(highlightPrefixText)
                            }
                            withStyle(SpanStyle(color = PrimBase)) {
                                append(highlightFoodName)
                            }
                        },
                        style = titleStyle,
                    )
                }
                Text(
                    text = description,
                    style = BarLabelTextStyle,
                    color = Gray200,
                )
            }
        },
    )
}

@Composable
private fun BaseBarChartCard(
    bars: List<BarItem>,
    modifier: Modifier = Modifier,
    headerChartSpacing: Dp = 32.dp,
    barSpacing: Dp,
    chartHeight: Dp,
    headerContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .insightCardContainer()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(headerChartSpacing),
    ) {
        headerContent()

        BarChart(
            bars = bars,
            barSpacing = barSpacing,
            chartHeight = chartHeight,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun BarChart(
    bars: List<BarItem>,
    barSpacing: Dp,
    chartHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val labelAreaHeight = BarLabelSpacing + BarLabelLineHeight.value.dp
    val chartAreaHeight = (chartHeight - labelAreaHeight).coerceAtLeast(MinChartAreaHeight)

    Box(
        modifier = modifier.height(chartHeight),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .align(Alignment.BottomCenter)
                .horizontalGridLinesFromBottom(
                    lineCount = ChartLineCount,
                    lineColor = Sd800,
                    chartAreaHeight = chartAreaHeight,
                    bottomReservedHeight = labelAreaHeight,
                ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = barSpacing,
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            bars.forEach { item ->
                BarGraphItem(
                    item = item,
                    barMaxHeight = chartAreaHeight,
                )
            }
        }
    }
}

@Composable
private fun BarGraphItem(
    item: BarItem,
    barMaxHeight: Dp,
) {
    val animatableRatio = remember(item) { Animatable(0f) }
    val clampedRatio = item.ratio.coerceIn(0f, 1f)

    LaunchedEffect(clampedRatio, item.animationDurationMillis, item.animationDelayMillis) {
        animatableRatio.snapTo(0f)
        if (item.animationDelayMillis > 0) {
            delay(item.animationDelayMillis.toLong())
        }
        animatableRatio.animateTo(
            targetValue = clampedRatio,
            animationSpec = tween(
                durationMillis = item.animationDurationMillis,
                easing = FastOutSlowInEasing,
            ),
        )
    }

    val minBarHeight = 12.dp
    val barHeight = (barMaxHeight * animatableRatio.value).coerceAtLeast(minBarHeight)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BarLabelSpacing),
    ) {
        Box(
            modifier = Modifier.height(barMaxHeight),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .width(BarWidth)
                    .height(barHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(item.topColor, item.bottomColor),
                        ),
                        shape = RoundedCornerShape(topStart = BarTopRadius, topEnd = BarTopRadius),
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    text = item.valueText,
                    modifier = Modifier.padding(top = ValueTopPadding),
                    style = BarLabelTextStyle,
                    color = White,
                )
            }
        }

        Text(
            text = item.label,
            modifier = Modifier.height(BarLabelLineHeight.value.dp),
            style = BarLabelTextStyle,
            color = Gray200,
        )
    }
}

private fun Modifier.insightCardContainer(): Modifier = background(
    color = White.copy(alpha = 0.02f),
    shape = RoundedCornerShape(16.dp),
)

@Preview
@Composable
private fun BarChartCardPreview() {
    FoodDiaryTheme {
        BarChartCard(
            title = "먹기 전에\n카메라부터 찾았네요.",
            descriptionPrefix = "지난 달 대비 기록된 사진이 ",
            highlightText = "70%",
            descriptionSuffix = " 증가했어요.",
            bars = listOf(
                BarItem(
                    label = "1월",
                    ratio = 20f / 140f,
                    valueText = "20",
                    topColor = Color(0xFF415199),
                    bottomColor = Color(0xFF8AA6E6),
                ),
                BarItem(
                    label = "2월",
                    ratio = 1f,
                    valueText = "140",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
            ).withStaggeredAnimation(delayStepMillis = 90),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun InsightFrequentFoodBarChartCardPreview() {
    FoodDiaryTheme {
        InsightFrequentFoodBarChartCard(
            title = "가장 자주 먹은",
            description = "고민은 길었고, 메뉴는 늘 비슷했어요.",
            highlightPrefixText = "음식은 ",
            highlightFoodName = "마라샹궈",
            bars = listOf(
                BarItem(
                    label = "1주차",
                    ratio = 0.5f,
                    valueText = "3회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarItem(
                    label = "2주차",
                    ratio = 0.6f,
                    valueText = "4회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarItem(
                    label = "3주차",
                    ratio = 0.1f,
                    valueText = "2회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarItem(
                    label = "4주차",
                    ratio = 0.9f,
                    valueText = "5회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarItem(
                    label = "5주차",
                    ratio = 1f,
                    valueText = "6회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
            ).withStaggeredAnimation(delayStepMillis = 70),
            modifier = Modifier.padding(16.dp),
        )
    }
}
