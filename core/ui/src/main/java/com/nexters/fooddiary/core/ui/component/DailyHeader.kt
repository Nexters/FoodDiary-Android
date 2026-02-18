package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.R
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.glassmorphism
import com.nexters.fooddiary.core.ui.util.toDailyHeaderText
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate

@Composable
fun DailyHeader(
    date: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    useGlassmorphism: Boolean = true,
) {
    val headerModifier = if (useGlassmorphism) {
        modifier.glassmorphism(hazeState = hazeState)
    } else {
        modifier
    }

    Row(
        modifier = headerModifier
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 화살표 버튼 (이전 날짜)
        IconButton(onClick = onPreviousDay) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "이전 날짜",
                tint = White
            )
        }

        // 중앙 날짜 텍스트
        Text(
            text = date.toDailyHeaderText(),
            style = AppTypography.hd18,
            fontWeight = FontWeight.Bold,
            color = White,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        // 우측 화살표 버튼 (다음 날짜)
        IconButton(onClick = onNextDay) {
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "다음 날짜",
                tint = White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun DailyHeaderPreview() {
    DailyHeader(
        date = LocalDate.of(2026, 1, 16),
        onPreviousDay = {},
        onNextDay = {}
    )
}
