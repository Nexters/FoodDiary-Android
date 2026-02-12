package com.nexters.fooddiary.core.ui

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

fun Modifier.gradientBorder(
    width: Dp,
    brush: Brush,
    shape: Shape,
) = border(width, brush, shape)