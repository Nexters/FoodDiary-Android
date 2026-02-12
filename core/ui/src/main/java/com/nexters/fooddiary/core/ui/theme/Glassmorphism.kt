package com.nexters.fooddiary.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class GlassmorphismStyle(
    val cornerRadius: Dp = 20.dp,
    val backgroundColor: Color = Color(0x4D515151),
    val borderWidth: Dp = 1.dp,
    val borderAngleDegrees: Float = 95.94f,
    val borderStops: List<Pair<Float, Color>> = listOf(
        0.1671f to Color.White.copy(alpha = 0.11f),
        0.6156f to Color.White.copy(alpha = 0f),
        1f to Color.White.copy(alpha = 0.05f),
    ),
    val blurRadius: Dp = 30.dp,
)

object GlassmorphismDefaults {
    val Default = GlassmorphismStyle()
}

fun Modifier.glassmorphism(
    hazeState: HazeState?,
    style: GlassmorphismStyle = GlassmorphismDefaults.Default,
): Modifier {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(style.cornerRadius)
    return this
        .clip(shape)
        .drawBehind {
            val cornerRadius = CornerRadius(style.cornerRadius.toPx(), style.cornerRadius.toPx())
            val strokeWidth = style.borderWidth.toPx()

            drawRoundRect(
                color = style.backgroundColor,
                cornerRadius = cornerRadius
            )

            val (start, end) = gradientOffsetsForAngle(size, style.borderAngleDegrees)
            val borderBrush = Brush.linearGradient(
                colorStops = style.borderStops.toTypedArray(),
                start = start,
                end = end
            )

            drawRoundRect(
                brush = borderBrush,
                cornerRadius = cornerRadius,
                style = Stroke(width = strokeWidth)
            )
        }
        .then(
            if (hazeState != null) {
                Modifier.hazeEffect(state = hazeState) {
                    backgroundColor = Color.Transparent
                    blurRadius = style.blurRadius
                }
            } else {
                Modifier
            }
        )
}

private fun gradientOffsetsForAngle(size: Size, angleDegrees: Float): Pair<Offset, Offset> {
    val radians = Math.toRadians(angleDegrees.toDouble())
    val directionX = sin(radians).toFloat()
    val directionY = -cos(radians).toFloat()
    val halfProjection = abs(directionX) * size.width / 2f + abs(directionY) * size.height / 2f

    val center = Offset(size.width / 2f, size.height / 2f)
    val start = Offset(
        x = center.x - directionX * halfProjection,
        y = center.y - directionY * halfProjection
    )
    val end = Offset(
        x = center.x + directionX * halfProjection,
        y = center.y + directionY * halfProjection
    )

    return start to end
}
