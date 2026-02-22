package com.nexters.fooddiary.core.ui.food

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.max
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val SwipeThreshold = 64.dp
private val AdditionalDropDistance = 96.dp
private val ClickCancelThreshold = 12.dp
private val FallbackDropDistance = 260.dp

private const val ResetDurationMillis = 180
private const val DropDurationMillis = 220
private const val RecycleDurationMillis = 260

private const val SecondCardBaseAlpha = 0.7f
private const val ThirdCardBaseAlpha = 0.4f
private const val HiddenAlpha = 0f
private const val VisibleAlpha = 1f

private const val PositiveRotation = 5f
private const val NegativeRotation = -5f

@Composable
fun FoodImageStackView(
    imageUrls: List<String>,
    state: FoodImageState,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (imageUrls.isEmpty()) return

    // 현재 맨 앞 카드 인덱스(순환)
    var currentIndex by rememberSaveable(imageUrls) { mutableIntStateOf(0) }
    val size = imageUrls.size
    val canNavigate = size > 1 && state is FoodImageState.Ready
    val frontIndex = loopedIndex(currentIndex, size)
    val backLeftIndex = loopedIndex(currentIndex + 1, size)
    val backRightIndex = loopedIndex(currentIndex + 2, size)
    val incomingBackIndex = loopedIndex(currentIndex + 3, size)
    val frontImageUrl = imageUrls.getOrNull(frontIndex)
    val backLeftImageUrl = imageUrls.getOrNull(backLeftIndex)
    val backRightImageUrl = imageUrls.getOrNull(backRightIndex)
    val incomingBackImageUrl = imageUrls.getOrNull(incomingBackIndex)
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val swipeThresholdPx = with(density) { SwipeThreshold.toPx() }
    val minAdditionalDropPx = with(density) { AdditionalDropDistance.toPx() }
    val tapCancelVerticalThresholdPx = with(density) { ClickCancelThreshold.toPx() }
    var stackHeightPx by remember { mutableIntStateOf(0) }
    val dropDistancePx = if (stackHeightPx > 0) stackHeightPx.toFloat() else with(density) { FallbackDropDistance.toPx() }
    var stackAnimationState by remember { mutableStateOf(StackAnimationState()) }
    var resetJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(imageUrls) {
        resetJob?.cancel()
        resetJob = null
        stackAnimationState = StackAnimationState()
    }

    fun animateFrontCardBackToOrigin() {
        if (stackAnimationState.frontOffset <= 0f) return
        resetJob?.cancel()
        resetJob = scope.launch {
            val reset = Animatable(stackAnimationState.frontOffset)
            reset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = ResetDurationMillis, easing = FastOutSlowInEasing),
            ) { stackAnimationState = stackAnimationState.copy(frontOffset = value) }
            resetJob = null
        }
    }

    // 앞 카드가 얼마나 내려갔는지(0~1)로 뒤 카드 상태를 연동
    val secondCardProgress = (stackAnimationState.frontOffset / dropDistancePx).coerceIn(0f, 1f)
    val incomingThirdAlpha = lerp(start = HiddenAlpha, stop = ThirdCardBaseAlpha, fraction = secondCardProgress)
    val thirdCardAlpha = lerp(start = ThirdCardBaseAlpha, stop = SecondCardBaseAlpha, fraction = secondCardProgress)
    val thirdCardRotation = lerp(start = NegativeRotation, stop = PositiveRotation, fraction = secondCardProgress)
    val secondCardAlpha = lerp(start = SecondCardBaseAlpha, stop = VisibleAlpha, fraction = secondCardProgress)
    val secondCardRotation = lerp(start = PositiveRotation, stop = 0f, fraction = secondCardProgress)

    Box(
        modifier = modifier
            .onSizeChanged { stackHeightPx = it.height }
            .pointerInput(canNavigate, size, stackAnimationState.isRecycling, dropDistancePx) {
                if (!canNavigate) return@pointerInput

                detectVerticalDragGestures(
                    // 1) 사용자가 앞 카드를 아래로 끌면 그대로 따라 내려감
                    onVerticalDrag = { change, dragAmount ->
                        if (stackAnimationState.isRecycling) return@detectVerticalDragGestures
                        resetJob?.cancel()
                        resetJob = null
                        val nextOffset = (stackAnimationState.frontOffset + dragAmount).coerceAtLeast(0f)
                        if (nextOffset != stackAnimationState.frontOffset) {
                            stackAnimationState = stackAnimationState.copy(frontOffset = nextOffset)
                            change.consume()
                        }
                    },
                    // 2) 드래그가 취소되면 현재 카드 위치를 원위치로 복귀
                    onDragCancel = {
                        if (stackAnimationState.isRecycling) return@detectVerticalDragGestures
                        animateFrontCardBackToOrigin()
                    },
                    // 3) 손을 뗐을 때 임계값으로 전환/복귀 분기
                    onDragEnd = {
                        if (stackAnimationState.isRecycling) return@detectVerticalDragGestures

                        if (stackAnimationState.frontOffset <= 0f) {
                            return@detectVerticalDragGestures
                        }

                        // 임계값 미만이면 스와이프 실패로 보고 원위치 복귀
                        if (stackAnimationState.frontOffset < swipeThresholdPx) {
                            animateFrontCardBackToOrigin()
                            return@detectVerticalDragGestures
                        }

                        scope.launch {
                            resetJob?.cancel()
                            resetJob = null
                            stackAnimationState = stackAnimationState.copy(isRecycling = true)
                            val outgoingIndex = frontIndex

                            // 임계값 이상이면 아래로 추가 하강(중간에서 놓아도 끝까지 내려가게 보장)
                            val dropTarget = max(
                                stackAnimationState.frontOffset + minAdditionalDropPx,
                                dropDistancePx,
                            )

                            val drop = Animatable(stackAnimationState.frontOffset)
                            drop.animateTo(
                                targetValue = dropTarget,
                                animationSpec = tween(durationMillis = DropDurationMillis, easing = FastOutSlowInEasing),
                            ) { stackAnimationState = stackAnimationState.copy(frontOffset = value) }

                            stackAnimationState = stackAnimationState.copy(
                                recycleIndex = outgoingIndex,
                                recycleOffset = dropTarget,
                                recycleRotation = 0f,
                                recycleAlpha = VisibleAlpha,
                            )

                            // 4) 실제 데이터 인덱스를 다음 카드로 넘겨 스택 순서를 갱신
                            currentIndex = loopedIndex(currentIndex + 1, size)
                            stackAnimationState = stackAnimationState.copy(frontOffset = 0f)

                            // 5) 내려간 기존 앞 카드를 뒤 스택 상태(alpha/angle)로 복귀 애니메이션
                            val recycleTargetOffsetPx = 0f
                            val targetRotation = recycleTargetRotation(size)
                            val targetAlpha = recycleTargetAlpha(size)
                            val startOffset = stackAnimationState.recycleOffset
                            val startRotation = stackAnimationState.recycleRotation
                            val startAlpha = stackAnimationState.recycleAlpha

                            val recycle = Animatable(0f)
                            recycle.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = RecycleDurationMillis, easing = FastOutSlowInEasing),
                            ) {
                                stackAnimationState = stackAnimationState.copy(
                                    recycleOffset = lerp(startOffset, recycleTargetOffsetPx, value),
                                    recycleRotation = lerp(startRotation, targetRotation, value),
                                    recycleAlpha = lerp(startAlpha, targetAlpha, value),
                                )
                            }

                            stackAnimationState = stackAnimationState.copy(
                                recycleIndex = null,
                                recycleOffset = 0f,
                                recycleRotation = 0f,
                                recycleAlpha = VisibleAlpha,
                                isRecycling = false,
                            )
                        }
                    },
                )
            }
    ) {
        // (4장 이상) 다음 턴에 3번째가 될 카드를 미리 깔아두고 점진 노출
        if (size >= 4 && stackAnimationState.recycleIndex != incomingBackIndex && incomingBackImageUrl != null) {
            FoodImageCard(
                imageUrl = incomingBackImageUrl,
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = NegativeRotation
                        alpha = incomingThirdAlpha
                    }
            )
        }

        // 현재 3번째 카드: 0번째가 내려갈수록 2번째 카드 상태로 준비
        if (size >= 3 && stackAnimationState.recycleIndex != backRightIndex && backRightImageUrl != null) {
            FoodImageCard(
                imageUrl = backRightImageUrl,
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
        if (size >= 2 && stackAnimationState.recycleIndex != backLeftIndex && backLeftImageUrl != null) {
            FoodImageCard(
                imageUrl = backLeftImageUrl,
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
        stackAnimationState.recycleIndex?.let { index ->
            imageUrls.getOrNull(index)?.let { recycleImageUrl ->
                FoodImageCard(
                    imageUrl = recycleImageUrl,
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = stackAnimationState.recycleOffset
                            rotationZ = stackAnimationState.recycleRotation
                            alpha = stackAnimationState.recycleAlpha
                        }
                )
            }
        }

        // 맨 앞 카드(사용자가 직접 끌어내리는 카드)
        frontImageUrl?.let { imageUrl ->
            FoodImageCard(
                imageUrl = imageUrl,
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = stackAnimationState.frontOffset
                    }
                    .clickable(
                        enabled = state is FoodImageState.Ready &&
                            !stackAnimationState.isRecycling &&
                            stackAnimationState.frontOffset <= tapCancelVerticalThresholdPx,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCardClick,
                    ),
            )
        }

        if (stackAnimationState.isRecycling) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInteropFilter { true }
            )
        }
    }
}

private fun loopedIndex(index: Int, size: Int): Int = ((index % size) + size) % size

private fun recycleTargetRotation(size: Int): Float = if (size == 2) PositiveRotation else NegativeRotation

private fun recycleTargetAlpha(size: Int): Float = when (size) {
    2 -> SecondCardBaseAlpha
    3 -> ThirdCardBaseAlpha
    else -> HiddenAlpha
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
