package com.nexters.fooddiary.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import com.nexters.fooddiary.core.ui.theme.neonShadow

@Composable
fun NeonStyleDay(
    topText: String = "DD",
    bottomText: String = "00",
    showDot: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFE670E), Color(0xFFFF853D))
    )

    val borderBrush = Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
    )

    Box(
        modifier = modifier
            .size(width = 40.dp, height = 56.dp)
            .neonShadow(
                color = Color(0x66FF8842),
                blurRadius = 16.dp,
                borderRadius = 8.dp
            )
            .border(width = 1.dp, brush = borderBrush, shape = RoundedCornerShape(8.dp))
            .background(brush = backgroundBrush, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // 정렬을 SpaceBetween으로 고정하여 점의 유무와 상관없이 텍스트 위치를 고정합니다.
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            // 점(Dot) 영역: showDot이 false여도 공간은 계속 차지합니다.
            Box(
                modifier = Modifier
                    .size(4.dp)
                    // 핵심: false일 때 투명도를 0으로 만들어 공간은 유지하되 숨깁니다.
                    .alpha(if (showDot) 1f else 0f)
                    .background(Color.White, shape = CircleShape)
            )

            // 텍스트 영역 (점의 상태와 상관없이 항상 같은 위치에 고정됨)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = topText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = bottomText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
fun NeonStyleDayComparisonPreview() {
    Row(
        modifier = Modifier.padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 점이 있는 기본 상태
        NeonStyleDay(topText = "DD", bottomText = "01", showDot = true)

        // 2. 점이 없는 상태
        NeonStyleDay(topText = "DD", bottomText = "02", showDot = false)
    }
}