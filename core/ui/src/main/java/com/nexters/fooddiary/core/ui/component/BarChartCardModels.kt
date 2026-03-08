package com.nexters.fooddiary.core.ui.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class BarChartItem(
    val label: String,
    val percentage: Float,
    val valueText: String,
    val topColor: Color,
    val bottomColor: Color,
    val animationDurationMillis: Int = 900,
    val animationDelayMillis: Int = 0,
)

fun List<BarChartItem>.withStaggeredAnimation(
    delayStepMillis: Int = 100,
    startDelayMillis: Int = 0,
): List<BarChartItem> {
    if (isEmpty()) return this

    val safeDelayStep = delayStepMillis.coerceAtLeast(0)
    val safeStartDelay = startDelayMillis.coerceAtLeast(0)
    if (safeDelayStep == 0 && safeStartDelay == 0) return this

    return mapIndexed { index, item ->
        item.copy(animationDelayMillis = safeStartDelay + (index * safeDelayStep))
    }
}
