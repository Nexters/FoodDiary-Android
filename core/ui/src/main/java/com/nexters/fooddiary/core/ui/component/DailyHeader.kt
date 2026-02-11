package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.PretendardFontFamily
import com.nexters.fooddiary.core.ui.theme.White
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun DailyHeader(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    Text(
        text = formatDateHeader(date),
        style = AppTypography.hd18,
        fontWeight = FontWeight.Bold,
        fontFamily = PretendardFontFamily,
        color = White,
        modifier = modifier.padding(vertical = 16.dp)
    )
}

private fun formatDateHeader(date: LocalDate): String {
    val dayOfWeek = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
        DayOfWeek.SUNDAY -> "일"
    }
    return "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일 ($dayOfWeek)"
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun DailyHeaderPreview() {
    DailyHeader(date = LocalDate.of(2026, 1, 16))
}
