package com.nexters.fooddiary.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.R as CoreUiR
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.AppTextPrimary
import com.nexters.fooddiary.presentation.detail.util.toDailyHeaderText
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate

@Composable
internal fun DailyHeader(
    date: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {

    Row(
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(24.dp))
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = CoreUiR.drawable.ic_back),
                contentDescription = "이전 날짜",
                tint = Color.Black,
            )
        }

        Text(
            text = date.toDailyHeaderText(),
            style = AppTypography.p18,
            fontWeight = FontWeight.Medium,
            color = AppTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onNextDay) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = CoreUiR.drawable.ic_next),
                contentDescription = "다음 날짜",
                tint = Color.Black,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyHeaderPreview() {
    DailyHeader(
        date = LocalDate.of(2026, 1, 16),
        onPreviousDay = {},
        onNextDay = {}
    )
}
