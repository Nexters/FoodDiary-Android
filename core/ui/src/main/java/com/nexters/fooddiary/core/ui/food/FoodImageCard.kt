package com.nexters.fooddiary.core.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.nexters.fooddiary.core.ui.R
import com.nexters.fooddiary.core.ui.theme.GrayBase
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Shadow40
import com.nexters.fooddiary.core.ui.theme.TimeLocationBg
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.neonShadow
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun FoodImageCard(
    imageUrl: String,
    state: FoodImageState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(300.dp)
            .neonShadow(
                color = Shadow40,
                borderRadius = 20.dp,
                blurRadius = 18.dp,
                offset = Offset(0f, 3.dp.value)
            )
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 4.dp,
                color = White,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        when (state) {
            is FoodImageState.FullUI -> {
                FoodImageFullUI(
                    imageUrl = imageUrl,
                    timeText = state.timeText,
                    locationText = state.locationText,
                    placeText = state.placeText,
                    category = state.category,
                    keywords = state.keywords,
                    onSaveClick = state.onSaveClick,
                    onShareClick = state.onShareClick,
                )
            }
            is FoodImageState.Summary -> {
                FoodImageSummary(
                    imageUrl = imageUrl,
                    timeText = state.timeText,
                    locationText = state.locationText,
                )
            }
            is FoodImageState.FullBlur -> {
                FoodImageFullBlur(
                    imageUrl = imageUrl,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FoodImageFullUI(
    imageUrl: String,
    timeText: String,
    locationText: String,
    placeText: String,
    category: String,
    keywords: List<String>,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    val hazeState = rememberHazeState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지 (blur 원본)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White
                )
                .hazeSource(state = hazeState)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Food image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.preview_food),
                error = painterResource(R.drawable.preview_food),
                modifier = Modifier.fillMaxSize()
            )
        }

        // 상단: 시간 태그 + 위치 텍스트
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 시간 태그
            TagChip(
                text = timeText,
                backgroundColor = TimeLocationBg,
                textColor = White,
                fontSize = 10,
            )

            // 위치 태그
            TagChip(
                text = locationText,
                backgroundColor = TimeLocationBg,
                textColor = White,
                fontSize = 10,
            )
        }

        TagChip(
            text = category,
            backgroundColor = PrimBase,
            textColor = White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 72.dp, start = 22.dp) // 블러 영역 높이의 중간 지점
                .zIndex(1f) // 맨 앞으로
        )

        // 하단 블러 오버레이
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .hazeEffect(state = hazeState) {
                    blurRadius = 20.dp
                }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(horizontal = 22.dp, vertical = 22.dp)
        ) {
            // 좌측: 장소 + keywords
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 장소 텍스트
                Text(
                    text = placeText,
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // keywords (배경색 없음)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    keywords.forEach { keyword ->
                        Text(
                            text = keyword,
                            color = White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // 우측: 액션 아이콘들
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 복사
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_copy),
                            contentDescription = "copy",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "복사",
                        color = White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // 공유
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_share),
                            contentDescription = "Share",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "공유",
                        color = White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    fontSize: Int = 12,
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
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 12.sp
        )
    }
}

@Composable
private fun FoodImageSummary(
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
                placeholder = painterResource(R.drawable.preview_food),
                error = painterResource(R.drawable.preview_food),
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
            // 시간 태그
            TagChip(
                text = timeText,
                backgroundColor = TimeLocationBg,
                textColor = White,
                fontSize = 10,
            )

            // 위치 태그
            TagChip(
                text = locationText,
                backgroundColor = TimeLocationBg,
                textColor = White,
                fontSize = 10,
            )
        }
    }
}

@Composable
private fun FoodImageFullBlur(
    imageUrl: String,
) {
    val hazeState = rememberHazeState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .hazeSource(state = hazeState)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Food image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.preview_food),
                error = painterResource(R.drawable.preview_food),
                modifier = Modifier.fillMaxSize()
            )
        }

        // 전체 블러 오버레이
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeEffect(state = hazeState) {
                    blurRadius = 30.dp
                }
        )
    }
}

@Preview(
    name = "FullUI State",
    showBackground = true,
    backgroundColor = 0xFF222222
)
@Composable
private fun FoodImageCardFullUIPreview() {
    FoodImageCard(
        imageUrl = "https://picsum.photos/300",
        state = FoodImageState.FullUI(
            timeText = "07:00",
            locationText = "마포구",
            placeText = "호진이네",
            category = "중식",
            keywords = listOf("#양장피", "#어향동고"),
            onSaveClick = {},
            onShareClick = {},
        )
    )
}

@Preview(
    name = "Summary State",
    showBackground = true,
    backgroundColor = 0xFF222222
)
@Composable
private fun FoodImageCardSummaryPreview() {
    FoodImageCard(
        imageUrl = "https://picsum.photos/300",
        state = FoodImageState.Summary(
            timeText = "07:00",
            locationText = "마포구",
        )
    )
}

@Preview(
    name = "FullBlur State",
    showBackground = true,
    backgroundColor = 0xFF222222
)
@Composable
private fun FoodImageCardFullBlurPreview() {
    FoodImageCard(
        imageUrl = "https://picsum.photos/300",
        state = FoodImageState.FullBlur
    )
}
