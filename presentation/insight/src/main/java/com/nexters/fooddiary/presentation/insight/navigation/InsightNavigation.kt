package com.nexters.fooddiary.presentation.insight.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.insight.InsightScreen
import kotlinx.serialization.Serializable

@Serializable
object InsightRoute

fun NavGraphBuilder.insightScreen(
    onNavigateToMyPage: () -> Unit = {},
) {
    composable<InsightRoute> {
        InsightScreen(
            onNavigateToMyPage = onNavigateToMyPage,
        )
    }
}
