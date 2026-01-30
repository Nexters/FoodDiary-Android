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
import com.nexters.fooddiary.presentation.image.navigation.ImageRoute
import com.nexters.fooddiary.presentation.image.navigation.imageScreen
import com.nexters.fooddiary.presentation.home.navigation.HomeRoute
import com.nexters.fooddiary.presentation.home.navigation.homeScreen
import com.nexters.fooddiary.presentation.home.calendar.navigation.CalendarRoute
import com.nexters.fooddiary.presentation.home.calendar.navigation.calendarScreen
import com.nexters.fooddiary.presentation.splash.navigation.SplashRoute
import com.nexters.fooddiary.presentation.splash.navigation.splashScreen

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
    var hasNavigatedFromSplash by remember { mutableStateOf(false) }
    val startDestination = if (initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_IMAGE) {
        ImageRoute
    } else {
        SplashRoute
    }

    LaunchedEffect(authUiState?.signInError) {
        authUiState?.signInError?.let { error ->
            onShowSignInError(error)
        }
    }

    // Splash 이후 인증 상태 변경 감지 (Login 후 Home 이동, Logout 후 Login 이동)
    LaunchedEffect(authUiState?.isAuthenticated) {
        if (!hasNavigatedFromSplash) return@LaunchedEffect

        // 로그아웃 완료 시 signOutRequestId 리셋
        if (signOutRequestId > 0 && authUiState?.isAuthenticated == false) {
            signOutRequestId = 0
        }

        authUiState?.isAuthenticated?.let { isAuthenticated ->
            if (!isAuthenticated) {
                // 로그아웃 → LoginRoute로 이동
                navController.navigate(LoginRoute) {
                    popUpTo(0) { inclusive = false }
                    launchSingleTop = true
                }
            } else if (signOutRequestId == 0) {
                // 로그인 → HomeRoute로 이동 (단, 로그아웃 중이 아닐 때만)
                navController.navigate(HomeRoute) {
                    popUpTo(0) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        splashScreen(
            onNavigateToHome = {
                hasNavigatedFromSplash = true
                navController.navigate(HomeRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                hasNavigatedFromSplash = true
                navController.navigate(LoginRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            }
        )

        loginScreen(
            onAuthStateChange = { state ->
                authUiState = state
            },
            signOutRequestId = { signOutRequestId },
            deleteAccountRequestId = { deleteAccountRequestId }
        )

        homeScreen(
            onNavigateToImage = { navController.navigate(ImageRoute) },
            onSignOut = {
                signOutRequestId++
                navController.navigate(LoginRoute) {
                    popUpTo(0) { inclusive = false }
                    launchSingleTop = true
                }
            },
            onDeleteAccount = {
                deleteAccountRequestId++
                navController.navigate(LoginRoute) {
                    popUpTo(0) { inclusive = false }
                    launchSingleTop = true
                }
            },
            onNavigateToCalendar = { navController.navigate(CalendarRoute) }
        )
        calendarScreen()
        imageScreen(
            onClose = {
                if (!navController.popBackStack()) {
                    onFinish()
                }
            }
        )
    }
}

