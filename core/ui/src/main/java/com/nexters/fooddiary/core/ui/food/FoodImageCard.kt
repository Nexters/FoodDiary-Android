package com.nexters.fooddiary.core.ui.food

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray900
import com.nexters.fooddiary.core.ui.theme.TimeLocationBg
import com.nexters.fooddiary.core.ui.theme.White

// Dashed border modifier
private fun Modifier.dashedBorder() = this.clip(RoundedCornerShape(16.dp))
    .background(color = White.copy(alpha = 0.02f))
    .drawBehind {
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                )
            )
        }
        drawPath(
            path = path,
            color = Gray900,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(3.dp.toPx(), 3.dp.toPx()),
                    phase = 0f,
                ),
            ),
        )
    }

@Composable
fun FoodImageCard(
    imageUrl: String,
    state: FoodImageState,
    modifier: Modifier = Modifier,
) {
    // State에 따라 다른 border 적용
    val borderModifier = when (state) {
        is FoodImageState.Pending -> {
            Modifier.dashedBorder()
        }
        else -> {
            Modifier.border(
                width = 4.dp,
                color = White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(borderModifier)
    ) {
        when (state) {
            is FoodImageState.Ready -> {
                FoodImage(
                    imageUrl = imageUrl,
                    timeText = state.timeText,
                    locationText = state.locationText,
                )
            }
            is FoodImageState.Pending -> {
                FoodImagePending(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun TagChip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 18.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = AppTypography.p12,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun FoodImage(
    imageUrl: String,
    timeText: String,
    locationText: String,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Food image",
                contentScale = ContentScale.Crop,
                placeholder = previewPlaceholder(),
                error = previewPlaceholder(),
                modifier = Modifier.fillMaxSize()
            )
        }

        // 상단: 시간 + 위치 태그
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagChip(
                text = timeText,
                backgroundColor = TimeLocationBg,
                textColor = White,
            )
            TagChip(
                text = locationText,
                backgroundColor = TimeLocationBg,
                textColor = White,
            )
        }
    }
}

@Composable
private fun FoodImagePending(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = White.copy(alpha = 0.02f)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(drawable.img_analyze_food),
                contentDescription = null,
                modifier = Modifier.size(208.dp),
            )
            Text(
                text = stringResource(string.detail_food_analyze),
                style = AppTypography.p12,
                color = White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
            )
        }
    }
}

@Composable
private fun previewPlaceholder() = if (LocalInspectionMode.current) {
    null
} else {
    null
}

@Preview(
    name = "Ready State",
    showBackground = true,
    backgroundColor = 0xFF191821
)
@Composable
private fun FoodImageReadyPreview() {
    FoodImageCard(
        imageUrl = "https://picsum.photos/300",
        state = FoodImageState.Ready(
            timeText = "07:00",
            locationText = "마포구",
        ),
        modifier = Modifier.size(300.dp)
    )
}

@Preview(
    name = "Pending State",
    showBackground = true,
    backgroundColor = 0xFF191821
)
@Composable
private fun FoodImagePendingPreview() {
    FoodImageCard(
        imageUrl = "https://picsum.photos/300",
        state = FoodImageState.Pending,
        modifier = Modifier.size(300.dp)
    )
}
