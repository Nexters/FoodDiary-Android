package com.nexters.fooddiary.presentation.insight

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase
import androidx.compose.foundation.verticalScroll
import com.nexters.fooddiary.presentation.insight.donut.InsightHighlightCard

@Composable
fun InsightScreen(
    modifier: Modifier = Modifier,
    state: InsightScreenState = InsightScreenState(),
    onNavigateToMyPage: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    BackHandler(onBack = onBack)

    val card = state.highlightCard

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    ) {
        Header(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 20.dp, top = 38.dp, end = 20.dp),
            onClickMyPage = onNavigateToMyPage,
        )

        if (state.isReady && card != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 102.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                InsightHighlightCard(
                    card = card,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_ready_insight),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(42.dp))
                Text(
                    text = stringResource(id = R.string.insight_ready_message),
                    style = AppTypography.p15,
                    color = Gray050,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun InsightScreenPreview() {
    FoodDiaryTheme {
        InsightScreen()
    }
}

@Preview
@Composable
private fun InsightScreenReadyPreview() {
    FoodDiaryTheme {
        InsightScreen(state = sampleInsightReadyState())
    }
}