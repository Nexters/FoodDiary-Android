package com.nexters.fooddiary.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.theme.SdBase
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun InsightScreen(
    modifier: Modifier = Modifier,
    onNavigateToMyPage: () -> Unit = {},
) {
    val screenHazeState = rememberHazeState()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .hazeSource(screenHazeState)
            .background(SdBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            Header(
                modifier = Modifier.padding(vertical = 18.dp),
                onClickMyPage = onNavigateToMyPage,
            )
        }
    }
}

@Preview
@Composable
private fun InsightScreenPreview() {
    InsightScreen()
}
