package com.nexters.fooddiary.core.ui

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.Gray900

fun Modifier.gradientBorder(
    width: Dp,
    brush: Brush,
    shape: Shape,
) = border(width, brush, shape)

fun Modifier.dashBorder(): Modifier = drawWithContent {
    drawContent()
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                cornerRadius = CornerRadius(16.dp.toPx()),
            )
        )
    }
    drawPath(
        path = path,
        color = Gray900,
        style = Stroke(
            width = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(3.dp.toPx(), 3.dp.toPx()),
                phase = 0f,
            ),
        ),
    )
}