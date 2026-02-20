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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.max
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun FoodImageStackView(
    imageUrls: List<String>,
    state: FoodImageState,
    modifier: Modifier = Modifier,
) {
    if (imageUrls.isEmpty()) return

    // 현재 맨 앞 카드 인덱스(순환)
    var currentIndex by rememberSaveable(imageUrls) { mutableIntStateOf(0) }
    val size = imageUrls.size
    val canNavigate = size > 1 && state is FoodImageState.Ready
    fun loopedIndex(index: Int): Int = ((index % size) + size) % size
    val frontIndex = loopedIndex(currentIndex)
    val backLeftIndex = loopedIndex(currentIndex + 1)
    val backRightIndex = loopedIndex(currentIndex + 2)
    val incomingBackIndex = loopedIndex(currentIndex + 3)
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val swipeThresholdPx = with(density) { 64.dp.toPx() }
    val minAdditionalDropPx = with(density) { 96.dp.toPx() }
    var stackHeightPx by remember { mutableIntStateOf(0) }
    val dropDistancePx = if (stackHeightPx > 0) stackHeightPx.toFloat() else with(density) { 260.dp.toPx() }
    var frontDragOffsetY by remember { mutableFloatStateOf(0f) }
    var recycleIndex by remember { mutableStateOf<Int?>(null) }
    var recycleOffsetY by remember { mutableFloatStateOf(0f) }
    var recycleRotation by remember { mutableFloatStateOf(0f) }
    var recycleAlpha by remember { mutableFloatStateOf(1f) }
    var isRecycling by remember { mutableStateOf(false) }

    // 앞 카드가 얼마나 내려갔는지(0~1)로 뒤 카드 상태를 연동
    val secondCardProgress = (frontDragOffsetY / dropDistancePx).coerceIn(0f, 1f)
    val incomingThirdAlpha = lerp(start = 0f, stop = 0.4f, fraction = secondCardProgress)
    val thirdCardAlpha = lerp(start = 0.4f, stop = 0.7f, fraction = secondCardProgress)
    val thirdCardRotation = lerp(start = -5f, stop = 5f, fraction = secondCardProgress)
    val secondCardAlpha = lerp(start = 0.7f, stop = 1f, fraction = secondCardProgress)
    val secondCardRotation = lerp(start = 5f, stop = 0f, fraction = secondCardProgress)

    Box(
        modifier = modifier
            .onSizeChanged { stackHeightPx = it.height }
            .pointerInput(canNavigate, size, isRecycling, dropDistancePx) {
                if (!canNavigate) return@pointerInput

                detectVerticalDragGestures(
                    // 1) 사용자가 앞 카드를 아래로 끌면 그대로 따라 내려감
                    onVerticalDrag = { change, dragAmount ->
                        if (isRecycling) return@detectVerticalDragGestures
                        val nextOffset = (frontDragOffsetY + dragAmount).coerceAtLeast(0f)
                        if (nextOffset != frontDragOffsetY) {
                            frontDragOffsetY = nextOffset
                            change.consume()
                        }
                    },
                    // 2) 드래그가 취소되면 현재 카드 위치를 원위치로 복귀
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
                    // 3) 손을 뗐을 때 임계값으로 전환/복귀 분기
                    onDragEnd = {
                        if (isRecycling) return@detectVerticalDragGestures

                        if (frontDragOffsetY <= 0f) {
                            return@detectVerticalDragGestures
                        }

                        // 임계값 미만이면 스와이프 실패로 보고 원위치 복귀
                        if (frontDragOffsetY < swipeThresholdPx) {
                            scope.launch {
                                val reset = Animatable(frontDragOffsetY)
                                reset.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                                ) { frontDragOffsetY = value }
                            }
                            return@detectVerticalDragGestures
                        }

                        scope.launch {
                            isRecycling = true
                            val outgoingIndex = frontIndex

                            // 임계값 이상이면 아래로 추가 하강(중간에서 놓아도 끝까지 내려가게 보장)
                            val dropTarget = max(
                                frontDragOffsetY + minAdditionalDropPx,
                                dropDistancePx,
                            )

                            val drop = Animatable(frontDragOffsetY)
                            drop.animateTo(
                                targetValue = dropTarget,
                                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                            ) { frontDragOffsetY = value }

                            recycleIndex = outgoingIndex
                            recycleOffsetY = dropTarget
                            recycleRotation = 0f
                            recycleAlpha = 1f

                            // 4) 실제 데이터 인덱스를 다음 카드로 넘겨 스택 순서를 갱신
                            currentIndex = loopedIndex(currentIndex + 1)
                            frontDragOffsetY = 0f

                            // 5) 내려간 기존 앞 카드를 뒤 스택 상태(alpha/angle)로 복귀 애니메이션
                            val recycleTargetOffsetPx = 0f
                            val recycleTargetRotation = when {
                                size == 2 -> 5f
                                size == 3 -> -5f
                                else -> -5f
                            }
                            val recycleTargetAlpha = when {
                                size == 2 -> 0.7f
                                size == 3 -> 0.4f
                                else -> 0f
                            }

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
        // (4장 이상) 다음 턴에 3번째가 될 카드를 미리 깔아두고 점진 노출
        if (size >= 4 && recycleIndex != incomingBackIndex) {
            FoodImageCard(
                imageUrl = imageUrls[incomingBackIndex],
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = -5f
                        alpha = incomingThirdAlpha
                    }
            )
        }

        // 현재 3번째 카드: 0번째가 내려갈수록 2번째 카드 상태로 준비
        if (size >= 3 && recycleIndex != backRightIndex) {
            FoodImageCard(
                imageUrl = imageUrls[backRightIndex],
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = thirdCardRotation
                        alpha = thirdCardAlpha
                    }
            )
        }

        // 현재 2번째 카드: 0번째가 내려갈수록 1번째 카드 상태로 준비
        if (size >= 2 && recycleIndex != backLeftIndex) {
            FoodImageCard(
                imageUrl = imageUrls[backLeftIndex],
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = secondCardRotation
                        alpha = secondCardAlpha
                    }
            )
        }

        // 내려갔던 기존 0번째 카드가 뒤 스택으로 돌아가는 전용 레이어
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

        // 맨 앞 카드(사용자가 직접 끌어내리는 카드)
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
