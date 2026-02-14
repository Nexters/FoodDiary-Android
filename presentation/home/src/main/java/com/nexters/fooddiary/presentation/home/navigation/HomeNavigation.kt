package com.nexters.fooddiary.presentation.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToImagePicker: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    showCoachmarkOnEntry: () -> Boolean = { false },
    onCoachmarkFlagConsumed: () -> Unit = {},
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToMyPage = onNavigateToMyPage,
            showCoachmarkOnEntry = showCoachmarkOnEntry(),
            onCoachmarkFlagConsumed = onCoachmarkFlagConsumed,
        )
    }
}
