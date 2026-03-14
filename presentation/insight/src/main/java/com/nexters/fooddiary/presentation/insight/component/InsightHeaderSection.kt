package com.nexters.fooddiary.presentation.insight.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.R
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
internal fun InsightHeaderSection(
    month: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = month.toInsightHeaderMonth(),
            style = AppTypography.p12,
            color = Gray050,
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = PrimBase)) {
                    append(stringResource(id = R.string.insight_header_highlight_title))
                }
                withStyle(SpanStyle(color = Gray050)) {
                    append(" ${stringResource(id = R.string.insight_header_title_suffix)}")
                }
            },
            style = AppTypography.hd20
        )
    }
}

private fun String.toInsightHeaderMonth(): String {
    val yearMonth = runCatching { YearMonth.parse(this) }.getOrNull()
    return yearMonth?.format(DateTimeFormatter.ofPattern("yyyy.MM")) ?: replace("-", ".")
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun InsightHeaderSectionPreview() {
    FoodDiaryTheme {
        Box(
            modifier = Modifier
                .background(SdBase)
                .padding(16.dp),
        ) {
            InsightHeaderSection(
                month = "2026-03",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
