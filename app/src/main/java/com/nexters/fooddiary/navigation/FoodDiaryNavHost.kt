package com.nexters.fooddiary.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.nexters.fooddiary.core.common.R
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
import com.nexters.fooddiary.presentation.mypage.navigation.MyPageRoute
import com.nexters.fooddiary.presentation.mypage.navigation.WebViewPage
import com.nexters.fooddiary.presentation.mypage.navigation.myPageScreen
import com.nexters.fooddiary.presentation.webview.navigation.WebViewRoute
import com.nexters.fooddiary.presentation.webview.navigation.webViewScreen

@Composable
fun FoodDiaryNavHost(
    initialDeepLink: Uri? = null,
    onFinish: () -> Unit,
    navController: NavHostController = rememberNavController(),
    onShowSignInError: (String) -> Unit
) {
    val context = LocalContext.current
    var authUiState by remember { mutableStateOf<AuthUiState?>(null) }
    var signOutRequestId by remember { mutableStateOf(0) }
    var deleteAccountRequestId by remember { mutableStateOf(0) }
    val startDestination = if (initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_IMAGE) {
        ImageRoute
    } else {
        HomeRoute
    }

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
            onNavigateToImage = { navController.navigate(ImageRoute) },
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
            },
            onNavigateToCalendar = { navController.navigate(CalendarRoute) },
            onNavigateToMyPage =  { navController.navigate(MyPageRoute)}
        )
        calendarScreen()
        imageScreen(
            onClose = {
                if (!navController.popBackStack()) {
                    onFinish()
                }
            }
        )
        myPageScreen(
            navigateToWebView = { page ->
                val url = when (page) {
                    WebViewPage.TermsOfService -> context.getString(R.string.webview_url_terms_of_service)
                    WebViewPage.PrivacyPolicy -> context.getString(R.string.webview_url_privacy_policy)
                }
                navController.navigate(WebViewRoute(url = url))
            },
            onSignOut = {
                signOutRequestId++
                navController.navigate(LoginRoute) {
                    popUpTo(HomeRoute) { inclusive = false }
                }
            },
            onRequireReAuthForDeleteAccount = {
                deleteAccountRequestId++
                navController.navigate(LoginRoute) {
                    popUpTo(HomeRoute) { inclusive = false }
                }
            }
        )
        webViewScreen(
            onClose = {
                if (!navController.popBackStack()) {
                    onFinish()
                }
            }
        )
    }
}

