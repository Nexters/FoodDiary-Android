package com.nexters.fooddiary.presentation.insight.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView

@Composable
internal fun AnimatedOnFirstVisible(
    modifier: Modifier = Modifier,
    content: @Composable (startAnimation: Boolean, modifier: Modifier) -> Unit,
) {
    if (LocalInspectionMode.current) {
        content(true, modifier)
        return
    }

    val view = LocalView.current
    var hasEnteredViewport by remember { mutableStateOf(false) }

    val viewportModifier = if (!hasEnteredViewport) {
        Modifier.onGloballyPositioned { coordinates ->
            val bounds = coordinates.boundsInWindow()
            val viewportHeight = view.height.toFloat()
            if (bounds.bottom > 0f && bounds.top < viewportHeight) {
                hasEnteredViewport = true
            }
        }
    } else {
        Modifier
    }

    content(hasEnteredViewport, modifier.then(viewportModifier))
}
