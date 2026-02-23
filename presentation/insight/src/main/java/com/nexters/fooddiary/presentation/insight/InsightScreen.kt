package com.nexters.fooddiary.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.fooddiary.core.ui.theme.SdBase

@Composable
fun InsightScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    )
}

@Preview
@Composable
private fun InsightScreenPreview() {
    InsightScreen()
}
