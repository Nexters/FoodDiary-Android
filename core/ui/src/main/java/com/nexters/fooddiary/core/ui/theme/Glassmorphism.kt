package com.nexters.fooddiary.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class GlassmorphismStyle(
    val cornerRadius: Dp = 20.dp,
    val backgroundColor: Color = Color(0x4D515151),
    val borderWidth: Dp = 2.2.dp,
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

@OptIn(ExperimentalHazeApi::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.glassmorphism(
    hazeState: HazeState?,
    style: GlassmorphismStyle = GlassmorphismDefaults.Default,
): Modifier {
    val hazeMaterialStyle = HazeMaterials.ultraThin(containerColor = SdBase)
    val shape = RoundedCornerShape(style.cornerRadius)
    return this
        .clip(shape)
        .drawBehind {
            val cornerRadius = CornerRadius(style.cornerRadius.toPx(), style.cornerRadius.toPx())

            drawRoundRect(
                color = style.backgroundColor,
                cornerRadius = cornerRadius
            )
        }
        .then(
            if (hazeState != null) {
                Modifier.hazeEffect(state = hazeState) {
                    this.style = hazeMaterialStyle
                }
            } else {
                Modifier
            }
        )
        .drawWithContent {
            drawContent()

            val cornerRadius = CornerRadius(style.cornerRadius.toPx(), style.cornerRadius.toPx())
            val strokeWidth = style.borderWidth.toPx()
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
