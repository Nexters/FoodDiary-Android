package com.nexters.fooddiary.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.nexters.fooddiary.R
import com.nexters.fooddiary.core.common.navigation.SyncConstants
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nexters.fooddiary.core.ui.alert.AppDialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.push.PushSyncEventBus
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
import com.nexters.fooddiary.presentation.modify.navigation.ModifyRoute
import com.nexters.fooddiary.presentation.modify.navigation.MODIFY_SEARCH_RESULT_ADDRESS_NAME
import com.nexters.fooddiary.presentation.modify.navigation.MODIFY_SEARCH_RESULT_NAME
import com.nexters.fooddiary.presentation.modify.navigation.MODIFY_SEARCH_RESULT_ROAD_ADDRESS
import com.nexters.fooddiary.presentation.modify.navigation.MODIFY_SEARCH_RESULT_URL
import com.nexters.fooddiary.presentation.modify.navigation.modifyScreen
import com.nexters.fooddiary.presentation.search.navigation.SearchRoute
import com.nexters.fooddiary.presentation.search.navigation.searchScreen
import com.nexters.fooddiary.presentation.webview.navigation.WebViewRoute
import com.nexters.fooddiary.presentation.webview.navigation.webViewScreen
import com.nexters.fooddiary.presentation.splash.navigation.SplashRoute
import com.nexters.fooddiary.presentation.splash.navigation.splashScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import com.nexters.fooddiary.core.common.R as CommonR
import com.nexters.fooddiary.core.ui.R as CoreUiR

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
    var deleteAccountRequestId by remember { mutableIntStateOf(0) }
    var onboardingCompleteEventId by remember { mutableIntStateOf(0) }
    var pendingDetailDate by remember { mutableStateOf(initialDeepLink.getDetailDateOrNull()) }
    var isHomeMonthlyCalendarView by rememberSaveable { mutableStateOf(false) }
    var hasNavigatedFromSplash by remember { mutableStateOf(false) }
    val bottomBarHazeState = rememberHazeState()
    var showHomeCoachmarkOnEntry by rememberSaveable { mutableStateOf(false) }

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
    val isLoginRoute =
        currentDestination?.hierarchy?.any { it.hasRoute(LoginRoute::class) } == true
    val shouldShowHomeInsightBottomBar = isHomeRoute || isInsightRoute
    val shouldHandleAppExitBack = isHomeRoute || isInsightRoute
    val selectedTab = if (isInsightRoute) HomeInsightTab.INSIGHT else HomeInsightTab.HOME
    var lastExitBackPressedAt by remember { mutableLongStateOf(0L) }

    val handleAppExitBackPress: () -> Unit = {
        val now = SystemClock.elapsedRealtime()
        if (now - lastExitBackPressedAt <= EXIT_CONFIRMATION_WINDOW_MILLIS) {
            onFinish()
        } else {
            lastExitBackPressedAt = now
            onShowToast(context.getString(R.string.exit_confirm_toast_message))
        }
    }

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

    LaunchedEffect(shouldHandleAppExitBack) {
        if (!shouldHandleAppExitBack) {
            lastExitBackPressedAt = 0L
        }
    }

    LaunchedEffect(Unit) {
        PushSyncEventBus.analysisCompleteEvents.collect { event ->
            val currentEntry = navController.currentBackStackEntry
            val isSyncTarget = currentEntry?.destination?.hierarchy?.any { destination ->
                destination.hasRoute(HomeRoute::class) || destination.hasRoute(DetailRoute::class)
            } == true
            if (isSyncTarget) {
                currentEntry.savedStateHandle[SyncConstants.DIARY_REFRESH_DATE] = event.diaryDate
            }
            onShowSnackBar(
                SnackBarData(
                    message = context.getString(R.string.push_analysis_complete_snackbar),
                    iconRes = CoreUiR.drawable.ic_ai_analysis
                )
            )
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

        // 회원탈퇴 재인증 흐름 완료 시 requestId 리셋
        if (deleteAccountRequestId > 0 && authUiState?.isAuthenticated == false) {
            deleteAccountRequestId = 0
        }

        authUiState?.isAuthenticated?.let { isAuthenticated ->
            if (!isAuthenticated) {
                // 로그아웃 → LoginRoute로 이동
                if (!isLoginRoute) { //이미 LoginRoute일때는 예외 처리
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            } else if (deleteAccountRequestId == 0) {
                // 로그인 → isFirst 체크해서 Onboarding 또는 Home으로 이동 (단, 회원탈퇴 재인증 중이 아닐 때만)
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
        BackHandler(enabled = isHomeRoute) {
            handleAppExitBackPress()
        }

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
                    onBack = handleAppExitBackPress,
                )

                detailScreen(
                    onDeleteSuccess = { deletedDate ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(SyncConstants.DIARY_REFRESH_DATE, deletedDate.toString())
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToImagePicker = { dateString ->
                        navController.navigate(ImagePickerRoute(dateString = dateString.toString()))
                    },
                    onNavigateToModify = { diaryId ->
                        navController.navigate(ModifyRoute(diaryId = diaryId))
                    },
                    onShowToast = onShowToast,
                )

                modifyScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToSearch = { query ->
                        navController.navigate(
                            SearchRoute(keyword = query.takeIf { it.isNotBlank() })
                        )
                    },
                    onShowDialog = { dialog -> onShowDialog(dialog) },
                    onShowSnackBar = onShowSnackBar,
                )

                imageScreen(
                    onClose = { selectedDateString ->
                        val previousIsDetail =
                            navController.previousBackStackEntry?.destination?.hasRoute(DetailRoute::class) == true
                        if (previousIsDetail && !selectedDateString.isNullOrBlank()) {
                            navController.popBackStack()
                            navController.popBackStack()
                            navController.navigate(DetailRoute(dateString = selectedDateString)) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onUploadSuccess = { uploadedDate ->
                        val previousIsHome =
                            navController.previousBackStackEntry?.destination?.hasRoute(HomeRoute::class) == true
                        val previousIsDetail =
                            navController.previousBackStackEntry?.destination?.hasRoute(DetailRoute::class) == true
                        if (previousIsHome) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(SyncConstants.DIARY_UPLOAD_PENDING_DATE, uploadedDate.toString())
                        }
                        navController.popBackStack()
                        if (previousIsDetail) {
                            navController.navigate(DetailRoute(dateString = uploadedDate.toString())) {
                                launchSingleTop = true
                            }
                        } else if (!previousIsHome) {
                            navController.navigate(HomeRoute) {
                                launchSingleTop = true
                            }
                        }
                    }
                )

                searchScreen(
                    onClose = {
                        if (!navController.popBackStack()) {
                            onFinish()
                        }
                    },
                    onSelectRestaurant = { restaurant ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            MODIFY_SEARCH_RESULT_NAME,
                            restaurant.name,
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            MODIFY_SEARCH_RESULT_ROAD_ADDRESS,
                            restaurant.roadAddress,
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            MODIFY_SEARCH_RESULT_ADDRESS_NAME,
                            restaurant.addressName.orEmpty(),
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            MODIFY_SEARCH_RESULT_URL,
                            restaurant.url,
                        )
                        navController.popBackStack()
                    }
                )

                myPageScreen(
                    navigateToWebView = { page ->
                        val url = when (page) {
                            WebViewPage.TermsOfService -> context.getString(CommonR.string.webview_url_terms_of_service)
                            WebViewPage.PrivacyPolicy -> context.getString(CommonR.string.webview_url_privacy_policy)
                        }
                        navController.navigate(WebViewRoute(url = url))
                    },
                    onShowDialog = onShowDialog,
                    onShowToast = onShowToast,
                    onBack = { navController.popBackStack() },
                    onSignOut = {
                        navController.navigate(LoginRoute) {
                            popUpTo(0) { inclusive = false }
                            launchSingleTop = true
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

private const val EXIT_CONFIRMATION_WINDOW_MILLIS = 2_000L
