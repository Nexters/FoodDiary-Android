package com.nexters.fooddiary.presentation.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToImage: () -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToImage = onNavigateToImage
        )
    }
}

