package com.nexters.fooddiary.presentation.widget

import androidx.compose.ui.unit.dp

internal object WidgetConstants {
    private const val DEEP_LINK_SCHEME = "fooddiary"
    private const val DEEP_LINK_HOST_CAMERA = "camera"
    const val DEEP_LINK_CAMERA = "$DEEP_LINK_SCHEME://$DEEP_LINK_HOST_CAMERA"
    
    val WIDGET_PADDING = 16.dp
    val WIDGET_CORNER_RADIUS = 16.dp
}

