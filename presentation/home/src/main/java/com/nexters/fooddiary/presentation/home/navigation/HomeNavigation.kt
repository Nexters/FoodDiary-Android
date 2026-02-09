package com.nexters.fooddiary.presentation.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToImage: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMyPage: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToCalendar = onNavigateToCalendar,
            onSignOut = onSignOut,
            onDeleteAccount = onDeleteAccount,
            onNavigateToImage = onNavigateToImage,
            onNavigateToMyPage = onNavigateToMyPage
        )
    }
}

