package com.nexters.fooddiary.core.ui.component

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
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
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
import kotlinx.coroutines.delay

private val BarLabelSpacing = 10.dp
private val BarLabelLineHeight = 14.sp
private val BarLabelTextStyle = AppTypography.p12.copy(
    fontSize = 10.sp,
    lineHeight = BarLabelLineHeight,
    letterSpacing = (-0.15).sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
)

@Immutable
data class InsightCompareBarItem(
    val label: String,
    val value: Int,
    val valueText: String = value.toString(),
    val topColor: Color,
    val bottomColor: Color,
    val animationDurationMillis: Int = 900,
    val animationDelayMillis: Int = 0,
)

fun List<InsightCompareBarItem>.withStaggeredAnimation(
    delayStepMillis: Int = 100,
    startDelayMillis: Int = 0,
): List<InsightCompareBarItem> = mapIndexed { index, item ->
    item.copy(animationDelayMillis = startDelayMillis + (index * delayStepMillis))
}

@Composable
fun InsightCompareBarChartCard(
    title: String,
    descriptionPrefix: String,
    highlightText: String,
    descriptionSuffix: String,
    bars: List<InsightCompareBarItem>,
    modifier: Modifier = Modifier,
) {
    val titleStyle = AppTypography.p15.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 21.sp,
        letterSpacing = (-0.225).sp,
    )
    val highlightStyle = titleStyle.copy(color = PrimBase)

    InsightBarChartCard(
        bars = bars,
        modifier = modifier,
        barSpacing = 60.dp,
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
                    style = AppTypography.p12,
                )
            }
        },
    )
}

@Composable
fun InsightFrequentFoodBarChartCard(
    title: String,
    description: String,
    highlightFoodName: String,
    bars: List<InsightCompareBarItem>,
    modifier: Modifier = Modifier,
) {
    val titleStyle = AppTypography.p15.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 21.sp,
        letterSpacing = (-0.225).sp,
    )
    val subtitleStyle = titleStyle

    InsightBarChartCard(
        bars = bars,
        modifier = modifier,
        barSpacing = 10.dp,
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
                                append("음식은 ")
                            }
                            withStyle(SpanStyle(color = PrimBase)) {
                                append(highlightFoodName)
                            }
                        },
                        style = subtitleStyle,
                    )
                }
                Text(
                    text = description,
                    style = AppTypography.p12.copy(
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        letterSpacing = (-0.15).sp,
                    ),
                    color = Gray200,
                )
            }
        },
    )
}

@Composable
private fun InsightBarChartCard(
    bars: List<InsightCompareBarItem>,
    modifier: Modifier = Modifier,
    headerChartSpacing: Dp = 32.dp,
    barSpacing: Dp = 60.dp,
    headerContent: @Composable () -> Unit,
) {
    val maxValue = bars.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1

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
            maxValue = maxValue,
            barSpacing = barSpacing,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun BarChart(
    bars: List<InsightCompareBarItem>,
    maxValue: Int,
    barSpacing: Dp,
    modifier: Modifier = Modifier,
) {
    val chartAreaHeight = 146.dp
    val labelAreaHeight = BarLabelSpacing + BarLabelLineHeight.value.dp

    Box(
        modifier = modifier.height(182.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(182.dp)
                .align(Alignment.BottomCenter)
                .horizontalGridLinesFromBottom(
                    lineCount = 5,
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
                BarItem(
                    item = item,
                    ratio = item.value / maxValue.toFloat(),
                    barMaxHeight = chartAreaHeight,
                )
            }
        }
    }
}

@Composable
private fun BarItem(
    item: InsightCompareBarItem,
    ratio: Float,
    barMaxHeight: androidx.compose.ui.unit.Dp,
) {
    val animatableRatio = remember(item.label) { Animatable(0f) }
    val clampedRatio = ratio.coerceIn(0f, 1f)
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
    val animatedRatio = animatableRatio.value
    val minBarHeight = 12.dp
    val barHeight = (barMaxHeight * animatedRatio).coerceAtLeast(minBarHeight)

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
                    .width(42.dp)
                    .height(barHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(item.topColor, item.bottomColor),
                        ),
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    text = item.valueText,
                    modifier = Modifier.padding(top = 8.dp),
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

private fun Modifier.horizontalGridLinesFromBottom(
    lineCount: Int,
    lineColor: Color,
    chartAreaHeight: androidx.compose.ui.unit.Dp,
    bottomReservedHeight: androidx.compose.ui.unit.Dp,
): Modifier = drawBehind {
    val chartAreaPx = chartAreaHeight.toPx()
    val bottomReservedPx = bottomReservedHeight.toPx()
    val zeroAxisY = size.height - bottomReservedPx
    val lineGap = chartAreaPx / lineCount

    repeat(lineCount + 1) { index ->
        val y = zeroAxisY - (lineGap * index)
        drawLine(
            color = lineColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx(),
        )
    }
}

@Preview
@Composable
private fun InsightCompareBarChartCardPreview() {
    FoodDiaryTheme {
        InsightCompareBarChartCard(
            title = "먹기 전에\n카메라부터 찾았네요.",
            descriptionPrefix = "지난 달 대비 기록된 사진이 ",
            highlightText = "70%",
            descriptionSuffix = " 증가했어요.",
            bars = listOf(
                InsightCompareBarItem(
                    label = "1월",
                    value = 20,
                    topColor = Color(0xFF415199),
                    bottomColor = Color(0xFF8AA6E6),
                    animationDelayMillis = 0,
                ),
                InsightCompareBarItem(
                    label = "2월",
                    value = 140,
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                    animationDelayMillis = 180,
                ),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun InsightCompareBarChartCardSmallGapPreview() {
    FoodDiaryTheme {
        InsightCompareBarChartCard(
            title = "사진 기록 습관이\n조금 늘었어요.",
            descriptionPrefix = "지난 주 대비 기록된 사진이 ",
            highlightText = "12%",
            descriptionSuffix = " 증가했어요.",
            bars = listOf(
                InsightCompareBarItem(
                    label = "이번 주",
                    value = 76,
                    topColor = Color(0xFF415199),
                    bottomColor = Color(0xFF8AA6E6),
                    animationDelayMillis = 0,
                ),
                InsightCompareBarItem(
                    label = "지난 주",
                    value = 68,
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                    animationDelayMillis = 180,
                ),
            ),
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
            highlightFoodName = "마라샹궈",
            bars = listOf(
                InsightCompareBarItem(
                    label = "1주차",
                    value = 3,
                    valueText = "3회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                InsightCompareBarItem(
                    label = "2주차",
                    value = 4,
                    valueText = "4회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                InsightCompareBarItem(
                    label = "3주차",
                    value = 2,
                    valueText = "2회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                InsightCompareBarItem(
                    label = "4주차",
                    value = 5,
                    valueText = "5회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                InsightCompareBarItem(
                    label = "5주차",
                    value = 6,
                    valueText = "6회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
            ).withStaggeredAnimation(delayStepMillis = 80),
            modifier = Modifier.padding(16.dp),
        )
    }
}
