package com.nexters.fooddiary.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nexters.fooddiary.presentation.auth.AuthUiState
import com.nexters.fooddiary.presentation.auth.navigation.LoginRoute
import com.nexters.fooddiary.presentation.auth.navigation.loginScreen
import com.nexters.fooddiary.presentation.camera.navigation.CameraRoute
import com.nexters.fooddiary.presentation.camera.navigation.cameraScreen
import com.nexters.fooddiary.presentation.home.navigation.HomeRoute
import com.nexters.fooddiary.presentation.home.navigation.homeScreen

@Composable
fun FoodDiaryNavHost(
    initialDeepLink: Uri? = null,
    onFinish: () -> Unit,
    navController: NavHostController = rememberNavController(),
    onShowSignInError: (String) -> Unit
) {
    var authUiState by remember { mutableStateOf<AuthUiState?>(null) }
    var signOutRequestId by remember { mutableStateOf(0) }
    var deleteAccountRequestId by remember { mutableStateOf(0) }

    LaunchedEffect(authUiState?.isAuthenticated) {
        authUiState?.isAuthenticated?.let { isAuthenticated ->
            if (isAuthenticated) {
                navController.navigate(HomeRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
            } else {
                navController.navigate(LoginRoute) {
                    popUpTo(HomeRoute) { inclusive = true }
                }
            }
        }
    }

    LaunchedEffect(authUiState?.signInError) {
        authUiState?.signInError?.let { error ->
            onShowSignInError(error)
        }
    }

    val startDestination = when {
        initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_CAMERA -> CameraRoute
        else -> LoginRoute
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        loginScreen(
            onAuthStateChange = { state ->
                authUiState = state
            },
            signOutRequestId = { signOutRequestId },
            deleteAccountRequestId = { deleteAccountRequestId }
        )

        homeScreen(
            onNavigateToCamera = { navController.navigate(CameraRoute) },
            onSignOut = {
                signOutRequestId++
                navController.navigate(LoginRoute) {
                    popUpTo(HomeRoute) { inclusive = false }
                }
            },
            onDeleteAccount = {
                deleteAccountRequestId++
                navController.navigate(LoginRoute) {
                    popUpTo(HomeRoute) { inclusive = false }
                }
            }
        )

        cameraScreen(
            onClose = {
                if (!navController.popBackStack()) {
                    onFinish()
                }
            }
        )
    }
}
