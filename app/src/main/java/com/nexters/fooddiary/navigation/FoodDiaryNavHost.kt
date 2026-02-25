package com.nexters.fooddiary.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nexters.fooddiary.core.common.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nexters.fooddiary.core.ui.alert.AppDialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.auth.AuthUiState
import com.nexters.fooddiary.presentation.auth.navigation.LoginRoute
import com.nexters.fooddiary.presentation.auth.navigation.loginScreen
import com.nexters.fooddiary.presentation.detail.navigation.DetailRoute
import com.nexters.fooddiary.presentation.detail.navigation.detailScreen
import com.nexters.fooddiary.presentation.home.HomeCoachmarkOverlay
import com.nexters.fooddiary.presentation.home.navigation.HomeRoute
import com.nexters.fooddiary.presentation.home.navigation.homeScreen
import com.nexters.fooddiary.presentation.insight.navigation.InsightRoute
import com.nexters.fooddiary.presentation.insight.navigation.insightScreen
import com.nexters.fooddiary.presentation.image.navigation.ImagePickerRoute
import com.nexters.fooddiary.presentation.image.navigation.imageScreen
import com.nexters.fooddiary.presentation.onboarding.navigation.OnboardingRoute
import com.nexters.fooddiary.presentation.onboarding.navigation.onboardingScreen
import com.nexters.fooddiary.presentation.mypage.navigation.MyPageRoute
import com.nexters.fooddiary.presentation.mypage.navigation.WebViewPage
import com.nexters.fooddiary.presentation.mypage.navigation.myPageScreen
import com.nexters.fooddiary.presentation.webview.navigation.WebViewRoute
import com.nexters.fooddiary.presentation.webview.navigation.webViewScreen
import com.nexters.fooddiary.presentation.splash.navigation.SplashRoute
import com.nexters.fooddiary.presentation.splash.navigation.splashScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import androidx.compose.ui.res.stringResource

@Composable
fun FoodDiaryNavHost(
    initialDeepLink: Uri? = null,
    onFinish: () -> Unit,
    navController: NavHostController = rememberNavController(),
    onShowDialog: (AppDialogData) -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
    onShowToast: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    var authUiState by remember { mutableStateOf<AuthUiState?>(null) }
    var signOutRequestId by remember { mutableIntStateOf(0) }
    var deleteAccountRequestId by remember { mutableIntStateOf(0) }
    var onboardingCompleteEventId by remember { mutableIntStateOf(0) }
    var pendingDetailDate by remember { mutableStateOf(initialDeepLink.getDetailDateOrNull()) }
    var isHomeMonthlyCalendarView by remember { mutableStateOf(false) }
    var hasNavigatedFromSplash by remember { mutableStateOf(false) }
    val bottomBarHazeState = rememberHazeState()
    var showHomeCoachmarkOnEntry by remember { mutableStateOf(false) }

    val startDestination = if (initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_IMAGE) {
        ImagePickerRoute(dateString = null)
    } else {
        SplashRoute
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isHomeRoute =
        currentDestination?.hierarchy?.any { it.hasRoute(HomeRoute::class) } == true
    val isInsightRoute =
        currentDestination?.hierarchy?.any { it.hasRoute(InsightRoute::class) } == true
    val shouldShowHomeInsightBottomBar = isHomeRoute || isInsightRoute
    val selectedTab = if (isInsightRoute) HomeInsightTab.INSIGHT else HomeInsightTab.HOME

    val navigateToPendingDetailIfNeeded: () -> Unit = {
        pendingDetailDate?.let { detailDate ->
            navController.navigate(DetailRoute(dateString = detailDate))
            pendingDetailDate = null
        }
    }

    fun navigateToImagePicker(dateString: String?) {
        navController.navigate(ImagePickerRoute(dateString = dateString))
    }

    LaunchedEffect(initialDeepLink) {
        initialDeepLink.getDetailDateOrNull()?.let { date ->
            pendingDetailDate = date
            if (hasNavigatedFromSplash) {
                navigateToPendingDetailIfNeeded()
            }
        }
    }

    LaunchedEffect(authUiState?.signInError) {
        authUiState?.signInError?.let { error ->
            onShowToast(error)
        }
    }

    LaunchedEffect(onboardingCompleteEventId) {
        if (onboardingCompleteEventId == 0) return@LaunchedEffect

        val isNotificationPermissionNeeded =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
        if (isNotificationPermissionNeeded) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                if (destination == HomeRoute) {
                    onboardingCompleteEventId += 1
                } else {
                    showHomeCoachmarkOnEntry = false
                }
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = false }
                    launchSingleTop = true
                }
                if (destination == HomeRoute) {
                    navigateToPendingDetailIfNeeded()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (shouldShowHomeInsightBottomBar) {
                    HomeInsightBottomBar(
                        selectedTab = selectedTab,
                        isMonthlyCalendarView = isHomeMonthlyCalendarView,
                        showCalendarToggle = isHomeRoute,
                        onToggleClick = {
                            if (selectedTab == HomeInsightTab.HOME) {
                                navController.navigate(InsightRoute) {
                                    launchSingleTop = true
                                }
                            } else {
                                val movedBackToHome = navController.popBackStack()
                                if (!movedBackToHome) {
                                    navController.navigate(HomeRoute) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                        onCalendarViewToggle = {
                            if (isHomeRoute) {
                                isHomeMonthlyCalendarView = !isHomeMonthlyCalendarView
                            }
                        },
                        hazeState = bottomBarHazeState,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(start = 20.dp, top = 26.dp, end = 20.dp, bottom = 24.dp)
                    )
                }
            }
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(bottomBarHazeState),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                splashScreen(
                    onNavigateToHome = {
                        hasNavigatedFromSplash = true
                        showHomeCoachmarkOnEntry = false
                        onboardingCompleteEventId += 1
                        navController.navigate(HomeRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                        }
                        navigateToPendingDetailIfNeeded()
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
                        showHomeCoachmarkOnEntry = true
                        onboardingCompleteEventId += 1
                        navController.navigate(HomeRoute) {
                            popUpTo(OnboardingRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                        navigateToPendingDetailIfNeeded()
                    }
                )

                homeScreen(
                    onNavigateToImagePicker = { date ->
                        navController.navigate(ImagePickerRoute(dateString = date.toString()))
                    },
                    onNavigateToDetail = { date ->
                        navController.navigate(DetailRoute(dateString = date.toString()))
                    },
                    onNavigateToMyPage = { navController.navigate(MyPageRoute) },
                    isMonthlyCalendarView = { isHomeMonthlyCalendarView },
                    onShowSnackBar = onShowSnackBar,
                )

                insightScreen(
                    onNavigateToMyPage = { navController.navigate(MyPageRoute) },
                    onBack = onFinish,
                )

                detailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToImagePicker = { dateString ->
                        navController.navigate(ImagePickerRoute(dateString = dateString.toString()))
                    },
                    onShowToast = onShowToast,
                )

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

        if (isHomeRoute && showHomeCoachmarkOnEntry) {
            HomeCoachmarkOverlay(
                onDismiss = { showHomeCoachmarkOnEntry = false },
                hazeState = bottomBarHazeState,
                weeklyHeaderBounds = null,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
            )
        }
    }
}

private fun Uri?.getDetailDateOrNull(): String? {
    if (this?.host != NavigationConstants.DEEP_LINK_HOST_DETAIL) return null
    return getQueryParameter(NavigationConstants.DEEP_LINK_QUERY_DATE)
        ?.takeIf { it.isNotBlank() }
}
