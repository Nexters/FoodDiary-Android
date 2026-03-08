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
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.InsightChartDefaults
import com.nexters.fooddiary.presentation.insight.InsightDonutCardUiModel
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState

private val CardShape = RoundedCornerShape(16.dp)
private val CardBackgroundColor = Color.White.copy(alpha = 0.02f)

@Composable
internal fun InsightDonutCard(
    card: InsightDonutCardUiModel,
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
                    text = buildInsightHeadline(card),
                    style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }

        InsightDonutChartWithLabels(
            segments = card.segments,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun buildInsightHeadline(card: InsightDonutCardUiModel): AnnotatedString = buildAnnotatedString {
    val previousCategory = card.previousTopCategory
    val currentCategory = card.currentTopCategory

    if (previousCategory == currentCategory) {
        withStyle(SpanStyle(color = card.categoryColor(currentCategory))) {
            append(currentCategory)
        }
        withStyle(SpanStyle(color = Gray050)) {
            append(" 이 계속 1등이에요.")
        }
        return@buildAnnotatedString
    }

    withStyle(SpanStyle(color = card.categoryColor(previousCategory))) {
        append(previousCategory)
    }
    withStyle(SpanStyle(color = Gray050)) {
        append(" 대신 ")
    }
    withStyle(SpanStyle(color = card.categoryColor(currentCategory))) {
        append(currentCategory)
    }
    withStyle(SpanStyle(color = Gray050)) {
        append(" 이 1등이에요.")
    }
}

private fun InsightDonutCardUiModel.categoryColor(category: String): Color =
    segments.firstOrNull { it.label == category }?.color
        ?: when (category) {
            "양식" -> InsightChartDefaults.BlueSegmentColor
            "한식" -> PrimBase
            else -> Gray050
        }

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightDonutCardPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            sampleInsightReadyState().donutCard?.let { card ->
                InsightDonutCard(
                    card = card,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
