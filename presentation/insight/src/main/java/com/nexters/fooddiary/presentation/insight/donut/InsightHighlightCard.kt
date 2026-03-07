package com.nexters.fooddiary.presentation.insight.donut

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.InsightHighlightCardUiModel
import com.nexters.fooddiary.presentation.insight.InsightTextPartUiModel
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState

private val CardShape = RoundedCornerShape(16.dp)
private val CardBackgroundColor = Color.White.copy(alpha = 0.02f)

@Composable
internal fun InsightHighlightCard(
    card: InsightHighlightCardUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .background(CardBackgroundColor)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = card.title,
                    style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                    color = Gray050,
                )
                Text(
                    text = buildInsightHeadline(card.headlineParts),
                    style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Text(
                text = card.caption,
                style = AppTypography.p12.copy(fontSize = 10.sp, lineHeight = 14.sp),
                color = Gray200,
            )
        }

        InsightDonutChartWithLabels(
            segments = card.segments,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun buildInsightHeadline(parts: List<InsightTextPartUiModel>): AnnotatedString = buildAnnotatedString {
    parts.forEach { part ->
        withStyle(SpanStyle(color = part.color)) {
            append(part.text)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightHighlightCardPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            sampleInsightReadyState().highlightCard?.let { card ->
                InsightHighlightCard(
                    card = card,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}