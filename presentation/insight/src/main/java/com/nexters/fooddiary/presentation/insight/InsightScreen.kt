package com.nexters.fooddiary.presentation.insight

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.presentation.insight.component.InsightMealTimeCard
import com.nexters.fooddiary.core.ui.R as coreR

@Composable
fun InsightScreen(
    modifier: Modifier = Modifier,
    onNavigateToMyPage: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: InsightViewModel = mavericksViewModel(),
) {
    val uiState by viewModel.collectAsStateWithLifecycle()

    InsightScreen(
        uiState = uiState,
        modifier = modifier,
        onNavigateToMyPage = onNavigateToMyPage,
        onBack = onBack,
    )
}

@Composable
private fun InsightScreen(
    uiState: InsightState,
    modifier: Modifier = Modifier,
    onNavigateToMyPage: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    ) {
        Header(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 20.dp, top = 38.dp, end = 20.dp),
            leftIconResId = coreR.drawable.ic_app_icon,
            leftIconColorFilter = null,
            onClickMyPage = onNavigateToMyPage,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 104.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            InsightMealTimeCard(
                highlightText = uiState.peakMealTime,
                descriptionText = stringResource(id = R.string.insight_meal_time_description),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun InsightScreenPreview() {
    InsightScreen(
        uiState = InsightState(),
    )
}
