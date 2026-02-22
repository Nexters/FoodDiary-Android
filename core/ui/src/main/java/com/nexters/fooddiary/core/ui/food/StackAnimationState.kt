package com.nexters.fooddiary.core.ui.food

import androidx.compose.runtime.Stable

private const val VisibleAlpha = 1f
@Stable
internal data class StackAnimationState(
    var frontOffset: Float = 0f,
    var recycleIndex: Int? = null,
    var recycleOffset: Float = 0f,
    var recycleRotation: Float = 0f,
    var recycleAlpha: Float = VisibleAlpha,
    var isRecycling: Boolean = false,
)