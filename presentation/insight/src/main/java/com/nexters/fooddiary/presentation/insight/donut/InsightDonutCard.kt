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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Blue500
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.AppTextPrimary
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.AppBackground
import com.nexters.fooddiary.core.ui.theme.AppSurfaceOverlay
import com.nexters.fooddiary.presentation.insight.InsightDonutCardUiModel
import com.nexters.fooddiary.presentation.insight.R
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState

private val CardShape = RoundedCornerShape(16.dp)
private val CardBackgroundColor = AppSurfaceOverlay
private val DonutSecondCategoryColor = Blue500

@Composable
internal fun InsightDonutCard(
    card: InsightDonutCardUiModel,
    modifier: Modifier = Modifier,
    startAnimation: Boolean = true,
) {
    val changedTitle = stringResource(R.string.insight_donut_title_changed)
    val maintainedTitle = stringResource(R.string.insight_donut_title_maintained)
    val changedMiddle = stringResource(R.string.insight_donut_headline_changed_middle)
    val headlineSuffix = stringResource(R.string.insight_donut_headline_suffix)

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
                    text = buildInsightTitle(
                        card = card,
                        changedTitle = changedTitle,
                        maintainedTitle = maintainedTitle,
                    ),
                    style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTextPrimary,
                )
                Text(
                    text = buildInsightHeadline(
                        card = card,
                        changedMiddle = changedMiddle,
                        suffix = headlineSuffix,
                    ),
                    style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }

        InsightDonutChartWithLabels(
            segments = card.segments,
            modifier = Modifier.fillMaxWidth(),
            startAnimation = startAnimation,
        )
    }
}

internal fun buildInsightTitle(
    card: InsightDonutCardUiModel,
    changedTitle: String,
    maintainedTitle: String,
): String = if (card.previousTopCategory == card.currentTopCategory) {
    maintainedTitle
} else {
    changedTitle
}

internal fun buildInsightHeadline(
    card: InsightDonutCardUiModel,
    changedMiddle: String,
    suffix: String,
): AnnotatedString = buildAnnotatedString {
    val previousCategory = card.previousTopCategory
    val currentCategory = card.currentTopCategory

    if (previousCategory == currentCategory) {
        withStyle(SpanStyle(color = card.categoryColor(currentCategory))) {
            append(currentCategory)
        }
        withStyle(SpanStyle(color = AppTextPrimary)) {
            append(suffix)
        }
        return@buildAnnotatedString
    }

    withStyle(SpanStyle(color = card.categoryColor(previousCategory))) {
        append(previousCategory)
    }
    withStyle(SpanStyle(color = AppTextPrimary)) {
        append(" $changedMiddle ")
    }
    withStyle(SpanStyle(color = card.categoryColor(currentCategory))) {
        append(currentCategory)
    }
    withStyle(SpanStyle(color = AppTextPrimary)) {
        append(suffix)
    }
}

private fun InsightDonutCardUiModel.categoryColor(category: String): Color =
    when (category) {
        currentTopCategory -> PrimBase
        previousTopCategory -> DonutSecondCategoryColor
        else -> AppTextPrimary
    }

@Preview(showBackground = true)
@Composable
private fun InsightDonutCardPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(AppBackground)
                .padding(16.dp),
        ) {
            InsightDonutCard(
                card = sampleInsightReadyState().donutCard,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
