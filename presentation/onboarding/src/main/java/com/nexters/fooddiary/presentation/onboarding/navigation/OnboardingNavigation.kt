package com.nexters.fooddiary.presentation.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.onboarding.OnboardingScreen
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

fun NavGraphBuilder.onboardingScreen(
    onComplete: () -> Unit = {}
) {
    composable<OnboardingRoute> {
        OnboardingScreen(
            onComplete = onComplete
        )
    }
}
