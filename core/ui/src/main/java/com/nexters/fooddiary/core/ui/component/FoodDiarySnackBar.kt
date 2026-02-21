package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.R
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.GlassmorphismStyle
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.glassmorphism
import dev.chrisbanes.haze.HazeState


@Composable
fun FoodDiarySnackBar(
    message: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    hazeState: HazeState? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
            .glassmorphism(
                hazeState = hazeState,
                style = GlassmorphismStyle(
                    cornerRadius = 16.dp,
                    blurRadius = 30.dp,
                )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = message,
            style = AppTypography.p15.copy(
                color = White,
                letterSpacing = (-0.015 * 15).sp
            ),
            textAlign = TextAlign.Start
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun FoodDiarySnackBarPreview() {
    FoodDiaryTheme {
        Box(modifier = Modifier.padding(vertical = 16.dp)) {
            FoodDiarySnackBar(
                message = "아이콘이 있는 스낵바입니다.",
                iconRes = R.drawable.ic_check_circle
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun FoodDiarySnackBarNoIconPreview() {
    FoodDiaryTheme {
        Box(modifier = Modifier.padding(vertical = 16.dp)) {
            FoodDiarySnackBar(message = "아이콘이 없는 스낵바입니다.")
        }
    }
}
