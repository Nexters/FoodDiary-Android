package com.nexters.fooddiary.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neonShadow(
    color: Color,
    borderRadius: Dp = 8.dp,
    blurRadius: Dp = 16.dp,
    offset: Offset = Offset.Zero,
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()

        // Shadow 설정
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            offset.x,
            offset.y,
            color.toArgb()
        )

        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}
