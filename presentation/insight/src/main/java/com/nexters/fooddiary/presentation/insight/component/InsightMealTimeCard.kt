package com.nexters.fooddiary.presentation.insight.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.R

private val MealCardShape = RoundedCornerShape(16.dp)
private val MealCardBackgroundColor = Color(0x05FFFFFF)
private val MealTitleStyle = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold)
private val MealDescriptionStyle = AppTypography.p15.copy(
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = (-0.15).sp,
    fontWeight = FontWeight.Normal,
)
private val MealTimeStyle = AppTypography.hd24.copy(
    fontSize = 50.sp,
    lineHeight = 65.sp,
    letterSpacing = (-0.75).sp,
    fontWeight = FontWeight.Bold,
)

@Composable
fun InsightMealTimeCard(
    highlightText: String,
    descriptionText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = MealCardBackgroundColor,
                shape = MealCardShape,
            )
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = highlightText,
                        style = MealTitleStyle,
                        color = PrimBase,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.insight_highlight_summary_suffix),
                        style = MealTitleStyle,
                        color = Gray050,
                    )
                }
            }
            Text(
                text = descriptionText,
                style = MealDescriptionStyle,
                color = Gray200,
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = highlightText,
                style = MealTimeStyle,
                color = PrimBase,
            )
        }
    }
}

@Preview
@Composable
private fun InsightMealTimeCardPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            InsightMealTimeCard(
                highlightText = "19:00",
                descriptionText = "이 시간대에 음식 사진이 가장 많이 남았어요.",
            )
        }
    }
}
