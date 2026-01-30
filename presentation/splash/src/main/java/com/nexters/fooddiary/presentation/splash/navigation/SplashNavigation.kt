package com.nexters.fooddiary.presentation.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.splash.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

fun NavGraphBuilder.splashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    composable<SplashRoute> {
        SplashScreen(
            onNavigateToHome = onNavigateToHome,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}
