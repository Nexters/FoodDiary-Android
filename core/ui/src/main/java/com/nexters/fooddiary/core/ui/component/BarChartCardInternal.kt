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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.horizontalGridLinesFromBottom
import kotlinx.coroutines.delay

private const val ChartLineCount = 5
private val BarChartLabelTextStyleInternal = AppTypography.p12.copy(
    fontSize = 10.sp,
    lineHeight = BarChartCardDefaults.BarLabelLineHeight,
    letterSpacing = (-0.15).sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
)

internal val BarChartLabelTextStyle: TextStyle = BarChartLabelTextStyleInternal
internal val BarChartTitleTextStyle: TextStyle = AppTypography.p15.copy(
    fontWeight = FontWeight.SemiBold,
    lineHeight = 21.sp,
    letterSpacing = (-0.225).sp,
)

@Composable
internal fun BaseBarChartCard(
    bars: List<BarChartItem>,
    modifier: Modifier = Modifier,
    headerChartSpacing: Dp = 32.dp,
    barSpacing: Dp,
    chartHeight: Dp,
    startAnimation: Boolean = true,
    headerContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .barChartCardContainer()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(headerChartSpacing),
    ) {
        headerContent()

        BarChart(
            bars = bars,
            barSpacing = barSpacing,
            chartHeight = chartHeight,
            startAnimation = startAnimation,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun BarChart(
    bars: List<BarChartItem>,
    barSpacing: Dp,
    chartHeight: Dp,
    startAnimation: Boolean,
    modifier: Modifier = Modifier,
) {
    val labelAreaHeight =
        BarChartCardDefaults.BarLabelSpacing + BarChartCardDefaults.BarLabelLineHeight.value.dp
    val chartAreaHeight =
        (chartHeight - labelAreaHeight).coerceAtLeast(BarChartCardDefaults.MinChartAreaHeight)

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
                    startAnimation = startAnimation,
                )
            }
        }
    }
}

@Composable
private fun BarGraphItem(
    item: BarChartItem,
    barMaxHeight: Dp,
    startAnimation: Boolean,
) {
    val animatableRatio = remember(item) { Animatable(0f) }
    val clampedPercentage = item.percentage.coerceIn(0f, 100f)
    val targetRatio = clampedPercentage / 100f

    LaunchedEffect(
        startAnimation,
        targetRatio,
        item.animationDurationMillis,
        item.animationDelayMillis,
    ) {
        animatableRatio.snapTo(0f)
        if (!startAnimation) {
            return@LaunchedEffect
        }
        if (item.animationDelayMillis > 0) {
            delay(item.animationDelayMillis.toLong())
        }
        animatableRatio.animateTo(
            targetValue = targetRatio,
            animationSpec = tween(
                durationMillis = item.animationDurationMillis,
                easing = FastOutSlowInEasing,
            ),
        )
    }

    val minBarHeight = 24.dp
    val barHeight = (barMaxHeight * animatableRatio.value).coerceAtLeast(minBarHeight)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BarChartCardDefaults.BarLabelSpacing),
    ) {
        Box(
            modifier = Modifier.height(barMaxHeight),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .width(BarChartCardDefaults.BarWidth)
                    .height(barHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(item.topColor, item.bottomColor),
                        ),
                        shape = RoundedCornerShape(
                            topStart = BarChartCardDefaults.BarTopRadius,
                            topEnd = BarChartCardDefaults.BarTopRadius,
                        ),
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    text = item.valueText,
                    modifier = Modifier.padding(top = BarChartCardDefaults.ValueTopPadding),
                    style = BarChartLabelTextStyle,
                    color = White,
                )
            }
        }

        Text(
            text = item.label,
            modifier = Modifier.height(BarChartCardDefaults.BarLabelLineHeight.value.dp),
            style = BarChartLabelTextStyle,
            color = Gray200,
        )
    }
}

private fun Modifier.barChartCardContainer(): Modifier = background(
    color = White.copy(alpha = 0.02f),
    shape = RoundedCornerShape(16.dp),
)
