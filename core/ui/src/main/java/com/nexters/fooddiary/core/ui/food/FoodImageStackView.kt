package com.nexters.fooddiary.core.ui.food

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun FoodImageStackView(
    imageUrls: List<String>,
    state: FoodImageState,
    modifier: Modifier = Modifier,
) {
    if (imageUrls.isEmpty()) return

    var currentIndex by rememberSaveable(imageUrls) { mutableIntStateOf(0) }
    val size = imageUrls.size
    val canNavigate = size > 1 && state is FoodImageState.Ready
    fun loopedIndex(index: Int): Int = ((index % size) + size) % size
    val frontIndex = loopedIndex(currentIndex)
    val backLeftIndex = loopedIndex(currentIndex + 1)
    val backRightIndex = loopedIndex(currentIndex + 2)
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val swipeThresholdPx = with(density) { 64.dp.toPx() }
    val dropDistancePx = with(density) { 140.dp.toPx() }
    val recycleBackLeftOffsetPx = 0f
    val recycleBackRightOffsetPx = 0f
    var frontDragOffsetY by remember { mutableFloatStateOf(0f) }
    var recycleIndex by remember { mutableStateOf<Int?>(null) }
    var recycleOffsetY by remember { mutableFloatStateOf(0f) }
    var recycleRotation by remember { mutableFloatStateOf(0f) }
    var recycleAlpha by remember { mutableFloatStateOf(1f) }
    var isRecycling by remember { mutableStateOf(false) }
    val recycleToBackRight = size >= 3

    Box(
        modifier = modifier.pointerInput(canNavigate, size, isRecycling) {
            if (!canNavigate) return@pointerInput

            detectVerticalDragGestures(
                onVerticalDrag = { change, dragAmount ->
                    if (isRecycling) return@detectVerticalDragGestures
                    val nextOffset = (frontDragOffsetY + dragAmount).coerceAtLeast(0f)
                    if (nextOffset != frontDragOffsetY) {
                        frontDragOffsetY = nextOffset
                        change.consume()
                    }
                },
                onDragCancel = {
                    if (isRecycling) return@detectVerticalDragGestures
                    if (frontDragOffsetY > 0f) {
                        scope.launch {
                            val reset = Animatable(frontDragOffsetY)
                            reset.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                            ) { frontDragOffsetY = value }
                        }
                    }
                },
                onDragEnd = {
                    if (isRecycling) return@detectVerticalDragGestures

                    if (frontDragOffsetY < swipeThresholdPx) {
                        if (frontDragOffsetY > 0f) {
                            scope.launch {
                                val reset = Animatable(frontDragOffsetY)
                                reset.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                                ) { frontDragOffsetY = value }
                            }
                        }
                        return@detectVerticalDragGestures
                    }

                    scope.launch {
                        isRecycling = true
                        val outgoingIndex = frontIndex

                        val drop = Animatable(frontDragOffsetY)
                        drop.animateTo(
                            targetValue = dropDistancePx,
                            animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
                        ) { frontDragOffsetY = value }

                        recycleIndex = outgoingIndex
                        recycleOffsetY = dropDistancePx
                        recycleRotation = 0f
                        recycleAlpha = 1f

                        currentIndex = loopedIndex(currentIndex + 1)
                        frontDragOffsetY = 0f

                        val recycleTargetOffsetPx =
                            if (recycleToBackRight) recycleBackRightOffsetPx else recycleBackLeftOffsetPx
                        val recycleTargetRotation = if (recycleToBackRight) 5f else -5f
                        val recycleTargetAlpha = if (recycleToBackRight) 0.4f else 0.7f

                        coroutineScope {
                            launch {
                                val offsetAnim = Animatable(recycleOffsetY)
                                offsetAnim.animateTo(
                                    targetValue = recycleTargetOffsetPx,
                                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                                ) { recycleOffsetY = value }
                            }
                            launch {
                                val rotationAnim = Animatable(recycleRotation)
                                rotationAnim.animateTo(
                                    targetValue = recycleTargetRotation,
                                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                                ) { recycleRotation = value }
                            }
                            launch {
                                val alphaAnim = Animatable(recycleAlpha)
                                alphaAnim.animateTo(
                                    targetValue = recycleTargetAlpha,
                                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                                ) { recycleAlpha = value }
                            }
                        }

                        recycleIndex = null
                        recycleOffsetY = 0f
                        recycleRotation = 0f
                        recycleAlpha = 1f
                        isRecycling = false
                    }
                },
            )
        }
    ) {
        if (size >= 3 && recycleIndex != backRightIndex) {
            FoodImageCard(
                imageUrl = imageUrls[backRightIndex],
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = -5f
                        alpha = 0.4f
                    }
            )
        }

        if (size >= 2 && recycleIndex != backLeftIndex) {
            FoodImageCard(
                imageUrl = imageUrls[backLeftIndex],
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = 5f
                        alpha = 0.7f
                    }
            )
        }

        recycleIndex?.let { index ->
            FoodImageCard(
                imageUrl = imageUrls[index],
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = recycleOffsetY
                        rotationZ = recycleRotation
                        alpha = recycleAlpha
                    }
            )
        }

        FoodImageCard(
            imageUrl = imageUrls[frontIndex],
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = frontDragOffsetY
                },
        )
    }
}

@Preview(
    name = "Stack View",
    showBackground = true,
    backgroundColor = 0xFF191821
)
@Composable
private fun FoodImageStackViewPreview() {
    FoodImageStackView(
        imageUrls = listOf(
            "https://picsum.photos/300?1",
            "https://picsum.photos/300?2",
            "https://picsum.photos/300?3",
        ),
        state = FoodImageState.Ready(
            timeText = "07:00",
            locationText = "마포구",
        ),
        modifier = Modifier.size(300.dp),
    )
}
