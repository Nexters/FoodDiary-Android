package com.nexters.fooddiary.presentation.insight.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.insight.InsightScreen
import com.nexters.fooddiary.presentation.insight.sampleInsightReadyState
import kotlinx.serialization.Serializable

@Serializable
object InsightRoute

fun NavGraphBuilder.insightScreen(
    onNavigateToMyPage: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    composable<InsightRoute> {
        InsightScreen(
            state = sampleInsightReadyState(),
            onNavigateToMyPage = onNavigateToMyPage,
            onBack = onBack,
        )
    }
}
