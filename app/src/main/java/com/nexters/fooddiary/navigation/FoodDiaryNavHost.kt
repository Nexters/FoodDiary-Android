package com.nexters.fooddiary.navigation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.nexters.fooddiary.core.common.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.auth.AuthUiState
import com.nexters.fooddiary.presentation.auth.navigation.LoginRoute
import com.nexters.fooddiary.presentation.auth.navigation.loginScreen
import com.nexters.fooddiary.presentation.detail.navigation.DetailRoute
import com.nexters.fooddiary.presentation.detail.navigation.detailScreen
import com.nexters.fooddiary.presentation.home.calendar.navigation.calendarScreen
import com.nexters.fooddiary.presentation.home.navigation.HomeRoute
import com.nexters.fooddiary.presentation.home.navigation.homeScreen
import com.nexters.fooddiary.presentation.image.navigation.ImagePickerRoute
import com.nexters.fooddiary.presentation.image.navigation.imageScreen
import com.nexters.fooddiary.presentation.home.calendar.navigation.CalendarRoute
import com.nexters.fooddiary.presentation.onboarding.navigation.OnboardingRoute
import com.nexters.fooddiary.presentation.onboarding.navigation.onboardingScreen
import com.nexters.fooddiary.presentation.mypage.navigation.MyPageRoute
import com.nexters.fooddiary.presentation.mypage.navigation.WebViewPage
import com.nexters.fooddiary.presentation.mypage.navigation.myPageScreen
import com.nexters.fooddiary.presentation.search.navigation.searchScreen
import com.nexters.fooddiary.presentation.webview.navigation.WebViewRoute
import com.nexters.fooddiary.presentation.webview.navigation.webViewScreen
import com.nexters.fooddiary.presentation.splash.navigation.SplashRoute
import com.nexters.fooddiary.presentation.splash.navigation.splashScreen

@Composable
fun FoodDiaryNavHost(
    initialDeepLink: Uri? = null,
    onFinish: () -> Unit,
    navController: NavHostController = rememberNavController(),
    onShowDialog: (DialogData) -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
    onShowToast: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var authUiState by remember { mutableStateOf<AuthUiState?>(null) }
    var signOutRequestId by remember { mutableIntStateOf(0) }
    var deleteAccountRequestId by remember { mutableIntStateOf(0) }
    var hasNavigatedFromSplash by remember { mutableStateOf(false) }
    val startDestination = if (initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_IMAGE) {
        ImagePickerRoute
    } else {
        SplashRoute
    }

    LaunchedEffect(authUiState?.signInError) {
        authUiState?.signInError?.let { error ->
            onShowToast(error)
        }
    }

    // Splash 이후 인증 상태 변경 감지 (Login 후 Onboarding/Home 이동, Logout 후 Login 이동)
    LaunchedEffect(authUiState?.isAuthenticated, authUiState?.isFirst) {
        if (!hasNavigatedFromSplash) return@LaunchedEffect

        // 로그아웃/회원탈퇴 완료 시 requestId 리셋
        if (signOutRequestId > 0 && authUiState?.isAuthenticated == false) {
            signOutRequestId = 0
        }
        if (deleteAccountRequestId > 0 && authUiState?.isAuthenticated == false) {
            deleteAccountRequestId = 0
        }

        authUiState?.isAuthenticated?.let { isAuthenticated ->
            if (!isAuthenticated) {
                // 로그아웃 → LoginRoute로 이동
                navController.navigate(LoginRoute) {
                    popUpTo(0) { inclusive = false }
                    launchSingleTop = true
                }
            } else if (signOutRequestId == 0 && deleteAccountRequestId == 0) {
                // 로그인 → isFirst 체크해서 Onboarding 또는 Home으로 이동 (단, 로그아웃/회원탈퇴 중이 아닐 때만)
                val destination = if (authUiState?.isFirst == true) {
                    OnboardingRoute
                } else {
                    HomeRoute
                }
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
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

        onboardingScreen(
            onComplete = {
                navController.navigate(HomeRoute) {
                    popUpTo(OnboardingRoute) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )

        homeScreen(
            onNavigateToImagePicker = { navController.navigate(ImagePickerRoute) },
            onNavigateToDetail = { date ->
                navController.navigate(DetailRoute(dateString = date.toString()))
            },
            onNavigateToMyPage = { navController.navigate(MyPageRoute)}
        )

        detailScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToImagePicker = { navController.navigate(ImagePickerRoute) },
            onShowToast = onShowToast,
        )

        calendarScreen()

        imageScreen(
            onClose = {
                if (!navController.popBackStack()) {
                    onFinish()
                }
            }
        )
        searchScreen(
            onClose = {
                if (!navController.popBackStack()) {
                    onFinish()
                }
            },
            onSelectRestaurant = { restaurantItem ->
                navController.popBackStack()
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
            onShowDialog = onShowDialog,
            onShowToast = onShowToast,
            onBack = { navController.popBackStack() },
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
            },
            onNavigateToAlarmSettings = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
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
