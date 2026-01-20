package com.nexters.fooddiary.presentation.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.auth.AuthUiState
import com.nexters.fooddiary.presentation.auth.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

fun NavGraphBuilder.loginScreen(
    onAuthStateChange: (AuthUiState) -> Unit = {},
    signOutRequestId: () -> Int = { 0 },
    deleteAccountRequestId: () -> Int = { 0 }
) {
    composable<LoginRoute> {
        LoginScreen(
            onAuthStateChange = onAuthStateChange,
            signOutRequestId = signOutRequestId(),
            deleteAccountRequestId = deleteAccountRequestId()
        )
    }
}
