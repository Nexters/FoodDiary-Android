package com.nexters.fooddiary.presentation.splash

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.android.play.core.install.InstallException
import com.google.android.play.core.install.model.ActivityResult
import com.nexters.fooddiary.core.ui.alert.AppDialogData
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.presentation.splash.inappupdate.InAppUpdateDecision
import com.nexters.fooddiary.presentation.splash.inappupdate.PlayInAppUpdateCoordinator
import kotlinx.coroutines.launch
import com.nexters.fooddiary.core.ui.R as CoreR

@Composable
internal fun SplashScreen(
    modifier: Modifier = Modifier,
    splashViewModel: SplashViewModel = mavericksViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onShowDialog: (AppDialogData) -> Unit = {},
    onShowToast: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by splashViewModel.collectAsStateWithLifecycle()
    val inAppUpdateCoordinator = remember(context.applicationContext.packageName) {
        PlayInAppUpdateCoordinator(context = context.applicationContext)
    }
    var isNavigationGateOpen by rememberSaveable { mutableStateOf(false) }
    var isWaitingForImmediateResult by rememberSaveable { mutableStateOf(false) }
    var isShowingFlexibleCompletionDialog by rememberSaveable { mutableStateOf(false) }

    fun showCompleteFlexibleUpdateDialog() {
        if (isShowingFlexibleCompletionDialog) return
        isShowingFlexibleCompletionDialog = true
        isNavigationGateOpen = false
        onShowDialog(
            DialogData(
                title = context.getString(R.string.in_app_update_complete_title),
                message = context.getString(R.string.in_app_update_complete_message),
                confirmText = context.getString(R.string.in_app_update_complete_confirm),
                dismissText = context.getString(R.string.in_app_update_complete_dismiss),
                onConfirm = {
                    coroutineScope.launch {
                        val result = inAppUpdateCoordinator.completeFlexibleUpdate()
                        isShowingFlexibleCompletionDialog = false
                        if (result.isFailure) {
                            onShowToast(context.getString(R.string.in_app_update_failed))
                        }
                        isNavigationGateOpen = true
                    }
                },
                onDismiss = {
                    isShowingFlexibleCompletionDialog = false
                    isNavigationGateOpen = true
                    onShowToast(context.getString(R.string.in_app_update_postponed))
                }
            )
        )
    }

    val inAppUpdateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isWaitingForImmediateResult = false
            isNavigationGateOpen = true
            return@rememberLauncherForActivityResult
        }

        isWaitingForImmediateResult = false
        isNavigationGateOpen = true
        val message = if (
            result.resultCode ==
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED
        ) {
            context.getString(R.string.in_app_update_failed)
        } else {
            context.getString(R.string.in_app_update_postponed)
        }
        onShowToast(message)
    }

    DisposableEffect(inAppUpdateCoordinator) {
        val listener: (InAppUpdateDecision) -> Unit = { decision ->
            if (decision == InAppUpdateDecision.CompleteFlexible) {
                showCompleteFlexibleUpdateDialog()
            }
        }

        inAppUpdateCoordinator.registerListener(listener)
        onDispose {
            inAppUpdateCoordinator.unregisterListener()
        }
    }

    LaunchedEffect(Unit) {
        runCatching {
            inAppUpdateCoordinator.checkForUpdate(inAppUpdateLauncher)
        }.onSuccess { decision ->
            when (decision) {
                InAppUpdateDecision.None,
                is InAppUpdateDecision.Flexible -> {
                    isNavigationGateOpen = true
                }

                InAppUpdateDecision.CompleteFlexible -> {
                    showCompleteFlexibleUpdateDialog()
                }

                is InAppUpdateDecision.Immediate -> {
                    isWaitingForImmediateResult = true
                    isNavigationGateOpen = false
                }
            }
        }.onFailure {
            isNavigationGateOpen = true
            if (it.cause is InstallException) //스토어 외의 경로로 설치 시 Install Error(-10) 발생
                onShowToast(context.getString(R.string.in_app_update_check_failed))
        }
    }

    LaunchedEffect(uiState.navigationDestination, isNavigationGateOpen, isWaitingForImmediateResult) {
        if (!isNavigationGateOpen || isWaitingForImmediateResult) return@LaunchedEffect
        uiState.navigationDestination?.let { destination ->
            when (destination) {
                NavigationDestination.Home -> onNavigateToHome()
                NavigationDestination.Login -> onNavigateToLogin()
            }
            splashViewModel.consumeNavigation()
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF191821)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = CoreR.drawable.ic_app_main_logo_text),
                contentDescription = null,
                modifier = Modifier
                    .width(225.dp)
                    .height(82.dp),
            )

            Image(
                painter = painterResource(id = CoreR.drawable.img_app_title_image),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashContentPreview() {
    SplashContent()
}
