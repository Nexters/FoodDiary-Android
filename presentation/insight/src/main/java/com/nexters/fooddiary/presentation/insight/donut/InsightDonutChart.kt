package com.nexters.fooddiary.presentation.insight.donut

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.InsightDonutSegmentUiModel
import com.nexters.fooddiary.presentation.insight.InsightSegmentGradientUiModel
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// 아크 렌더링과 라벨 배치가 공통으로 사용하는 도넛 차트 기본 기하값.
private const val FullCircleDegrees = 360f
internal const val InsightChartStartAngle = -90f
internal const val InsightChartGapAngle = 1.5f
internal const val InsightChartInnerHoleRatio = 0.35f

// 차트 컨테이너의 최대 레이아웃 크기.
private val InsightChartMaxSize = 192.dp

// 링 내부 라벨을 그릴 때 사용하는 여백 값.
private val InsightChartLabelOuterPadding = 6.dp

// 넓은 세그먼트의 라벨을 조금 더 바깥쪽에 두기 위한 시각 보정값.
private const val InsightChartLabelOutwardBiasStartAngle = 140f
private const val InsightChartLabelOutwardBiasRange = 140f

// 차트 등장 시 아크와 라벨이 함께 드러나는 애니메이션 설정값.
private const val InsightChartRevealDurationMillis = 1100
private const val InsightChartLabelFadeStartProgress = 0.72f
private const val InsightChartRevealRotationDegrees = 120f

@Composable
internal fun InsightDonutChart(
    segments: List<InsightDonutSegmentUiModel>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = Dp.Unspecified,
    gapAngle: Float = InsightChartGapAngle,
    startAngle: Float = InsightChartStartAngle,
    innerHoleRatio: Float = InsightChartInnerHoleRatio,
    dividerColor: Color = SdBase,
    animationProgress: Float = 1f,
) {
    val validSegments = remember(segments) {
        segments.filter { it.value > 0f }
    }
    val geometries = remember(validSegments, startAngle) {
        calculateDonutSegmentGeometries(
            values = validSegments.map(InsightDonutSegmentUiModel::value),
            startAngle = startAngle,
            gapAngle = 0f,
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (geometries.isEmpty()) {
                return@Canvas
            }

            val resolvedInnerHoleRatio = innerHoleRatio.coerceIn(0f, 0.95f)
            val resolvedStrokeWidth = if (strokeWidth != Dp.Unspecified) {
                strokeWidth.toPx()
            } else {
                size.minDimension * (1f - resolvedInnerHoleRatio) / 2f
            }
            val brushBounds = Size(width = size.width, height = size.height)
            val inset = resolvedStrokeWidth / 2f
            val arcSize = Size(
                width = (size.width - resolvedStrokeWidth).coerceAtLeast(0f),
                height = (size.height - resolvedStrokeWidth).coerceAtLeast(0f),
            )
            val outerRadius = size.minDimension / 2f
            val innerRadius = (outerRadius - resolvedStrokeWidth).coerceAtLeast(0f)
            val dividerStrokeWidth = calculateDividerStrokeWidthPx(
                outerRadius = outerRadius,
                gapAngle = gapAngle,
            )
            val clampedProgress = animationProgress.coerceIn(0f, 1f)
            val animatedRotationOffset = (1f - clampedProgress) * InsightChartRevealRotationDegrees

            geometries.forEachIndexed { index, geometry ->
                drawArc(
                    brush = validSegments[index].gradient.toBrush(size = brushBounds),
                    startAngle = geometry.startAngle - animatedRotationOffset,
                    sweepAngle = geometry.sweepAngle * clampedProgress,
                    useCenter = false,
                    topLeft = Offset(x = inset, y = inset),
                    size = arcSize,
                    style = Stroke(width = resolvedStrokeWidth, cap = StrokeCap.Butt),
                )
            }

            if (clampedProgress >= 0.999f && geometries.size > 1 && dividerStrokeWidth > 0f) {
                geometries.forEach { geometry ->
                    val start = center + polarOffset(
                        radius = innerRadius,
                        angleInDegrees = geometry.startAngle,
                    )
                    val end = center + polarOffset(
                        radius = outerRadius,
                        angleInDegrees = geometry.startAngle,
                    )
                    drawLine(
                        color = dividerColor,
                        start = start,
                        end = end,
                        strokeWidth = dividerStrokeWidth,
                        cap = StrokeCap.Butt,
                    )
                }
            }
        }
    }
}

@Composable
internal fun InsightDonutChartWithLabels(
    segments: List<InsightDonutSegmentUiModel>,
    modifier: Modifier = Modifier,
) {
    val validSegments = remember(segments) {
        segments.filter { it.value > 0f }
    }
    val textMeasurer = rememberTextMeasurer()
    val animationProgress = rememberInsightChartRevealProgress(validSegments)

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val chartSize = if (maxWidth < InsightChartMaxSize) maxWidth else InsightChartMaxSize

        Box(
            modifier = Modifier
                .size(chartSize)
                .widthIn(max = InsightChartMaxSize),
            contentAlignment = Alignment.Center,
        ) {
            InsightDonutChart(
                segments = validSegments,
                modifier = Modifier.fillMaxSize(),
                gapAngle = InsightChartGapAngle,
                startAngle = InsightChartStartAngle,
                innerHoleRatio = InsightChartInnerHoleRatio,
                animationProgress = animationProgress,
            )

            InsightDonutChartLabels(
                segments = validSegments,
                chartSize = chartSize,
                textMeasurer = textMeasurer,
                animationProgress = animationProgress,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun InsightDonutChartLabels(
    segments: List<InsightDonutSegmentUiModel>,
    chartSize: Dp,
    textMeasurer: TextMeasurer,
    animationProgress: Float,
    modifier: Modifier = Modifier,
) {
    val geometries = remember(segments) {
        calculateDonutSegmentGeometries(
            values = segments.map(InsightDonutSegmentUiModel::value),
            startAngle = InsightChartStartAngle,
            gapAngle = 0f,
        )
    }

    Canvas(modifier = modifier) {
        if (segments.isEmpty() || geometries.isEmpty()) {
            return@Canvas
        }
        val labelAlpha = ((animationProgress - InsightChartLabelFadeStartProgress) /
            (1f - InsightChartLabelFadeStartProgress)).coerceIn(0f, 1f)
        if (labelAlpha <= 0f) {
            return@Canvas
        }
        val labelStyle = AppTypography.p12.copy(
            color = Gray050.copy(alpha = labelAlpha),
        )

        val chartSizePx = chartSize.toPx()
        val outerRadius = minOf(size.minDimension, chartSizePx) / 2f
        val innerRadius = outerRadius * InsightChartInnerHoleRatio
        val chartCenter = center
        val labelPadding = InsightChartLabelOuterPadding.toPx()

        segments.zip(geometries).forEach { (segment, geometry) ->
            val text = segment.valueText ?: segment.value.toInt().toString()
            val textLayoutResult = textMeasurer.measure(
                text = text,
                style = labelStyle,
            )
            val angleInRadians = Math.toRadians(geometry.midAngle.toDouble())
            val horizontalWeight = abs(cos(angleInRadians)).toFloat()
            val baseRadius = calculateDonutLabelRadius(
                outerRadius = outerRadius,
                innerRadius = innerRadius,
                sweepAngle = geometry.sweepAngle,
            )
            val maxOutwardShift = ((outerRadius - labelPadding) - baseRadius).coerceAtLeast(0f)
            val outwardShift = ((textLayoutResult.size.width / 2f) * horizontalWeight)
                .coerceAtMost(maxOutwardShift)
            val labelCenter = chartCenter + polarOffset(
                radius = baseRadius + outwardShift,
                angleInDegrees = geometry.midAngle,
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = labelCenter.x - (textLayoutResult.size.width / 2f),
                    y = labelCenter.y - (textLayoutResult.size.height / 2f),
                ),
            )
        }
    }
}

@Composable
private fun rememberInsightChartRevealProgress(
    segments: List<InsightDonutSegmentUiModel>,
): Float {
    val progress = remember { Animatable(0f) }
    val animationKey = remember(segments) {
        segments.map { "${it.label}:${it.value}:${it.valueText}" }
    }

    LaunchedEffect(animationKey) {
        progress.snapTo(0f)

        if (segments.isNotEmpty()) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = InsightChartRevealDurationMillis,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
    }

    return progress.value
}

internal data class DonutSegmentGeometry(
    val startAngle: Float,
    val sweepAngle: Float,
) {
    val midAngle: Float
        get() = startAngle + (sweepAngle / 2f)
}

internal fun calculateDonutSegmentGeometries(
    values: List<Float>,
    startAngle: Float,
    gapAngle: Float,
): List<DonutSegmentGeometry> {
    val normalizedValues = values.filter { it > 0f }
    if (normalizedValues.isEmpty()) {
        return emptyList()
    }

    if (normalizedValues.size == 1) {
        return listOf(
            DonutSegmentGeometry(
                startAngle = startAngle,
                sweepAngle = FullCircleDegrees,
            ),
        )
    }

    val totalValue = normalizedValues.sum()
    if (totalValue <= 0f) {
        return emptyList()
    }

    val totalGap = gapAngle.coerceAtLeast(0f) * normalizedValues.size
    val availableSweep = (FullCircleDegrees - totalGap).coerceAtLeast(0f)
    var currentStart = startAngle

    return normalizedValues.map { value ->
        val sweepAngle = (value / totalValue) * availableSweep
        DonutSegmentGeometry(
            startAngle = currentStart,
            sweepAngle = sweepAngle,
        ).also {
            currentStart += sweepAngle + gapAngle
        }
    }
}

internal fun calculateAnnularSectorCentroidRadius(
    outerRadius: Float,
    innerRadius: Float,
    sweepAngle: Float,
): Float {
    if (outerRadius <= innerRadius) {
        return (outerRadius + innerRadius) / 2f
    }

    val theta = Math.toRadians(abs(sweepAngle).toDouble())
    if (theta <= 0.0001 || theta >= (2.0 * PI)) {
        return (outerRadius + innerRadius) / 2f
    }

    val outerCubed = outerRadius * outerRadius * outerRadius
    val innerCubed = innerRadius * innerRadius * innerRadius
    val outerSquared = outerRadius * outerRadius
    val innerSquared = innerRadius * innerRadius

    val numerator = 4.0 * sin(theta / 2.0) * (outerCubed - innerCubed)
    val denominator = 3.0 * theta * (outerSquared - innerSquared)

    return if (denominator == 0.0) {
        (outerRadius + innerRadius) / 2f
    } else {
        (numerator / denominator).toFloat()
    }
}

internal fun calculateDonutLabelRadius(
    outerRadius: Float,
    innerRadius: Float,
    sweepAngle: Float,
): Float {
    val centroidRadius = calculateAnnularSectorCentroidRadius(
        outerRadius = outerRadius,
        innerRadius = innerRadius,
        sweepAngle = sweepAngle,
    )
    val ringMidRadius = (outerRadius + innerRadius) / 2f
    val outwardBias = (
        (abs(sweepAngle) - InsightChartLabelOutwardBiasStartAngle) /
            InsightChartLabelOutwardBiasRange
        ).coerceIn(0f, 1f)

    return centroidRadius + ((ringMidRadius - centroidRadius) * outwardBias)
}

internal fun calculateDividerStrokeWidthPx(
    outerRadius: Float,
    gapAngle: Float,
): Float {
    val clampedGapAngle = gapAngle.coerceAtLeast(0f)
    if (clampedGapAngle == 0f || outerRadius <= 0f) {
        return 0f
    }

    return outerRadius * Math.toRadians(clampedGapAngle.toDouble()).toFloat()
}

private fun polarOffset(
    radius: Float,
    angleInDegrees: Float,
): Offset {
    val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
    return Offset(
        x = (cos(angleInRadians) * radius).toFloat(),
        y = (sin(angleInRadians) * radius).toFloat(),
    )
}

private fun InsightSegmentGradientUiModel.toBrush(size: Size): Brush {
    if (colors.size <= 1) {
        return Brush.linearGradient(colors = colors.ifEmpty { listOf(Gray050) })
    }

    return Brush.linearGradient(
        colors = colors,
        start = Offset(
            x = size.width * start.xFraction,
            y = size.height * start.yFraction,
        ),
        end = Offset(
            x = size.width * end.xFraction,
            y = size.height * end.yFraction,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightDonutChartPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            InsightDonutChart(
                segments = sampleInsightReadyState().donutCard?.segments.orEmpty(),
                modifier = Modifier.size(InsightChartMaxSize),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightDonutChartWithLabelsPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            InsightDonutChartWithLabels(
                segments = sampleInsightReadyState().donutCard?.segments.orEmpty(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
