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
import com.nexters.fooddiary.presentation.splash.inappupdate.FlexibleInstallStateDecision
import com.nexters.fooddiary.presentation.splash.inappupdate.InitialInAppUpdateDecision
import com.nexters.fooddiary.presentation.splash.inappupdate.PlayInAppUpdateCoordinator
import io.sentry.Sentry
import kotlinx.coroutines.launch
import com.nexters.fooddiary.core.ui.R as CoreR

private const val PLAY_CORE_FAILED_TO_BIND_SERVICE_MESSAGE = "Failed to bind to the service."

@Composable
internal fun SplashScreen(
    modifier: Modifier = Modifier,
    splashViewModel: SplashViewModel = mavericksViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onFinish: () -> Unit,
    onShowDialog: (AppDialogData) -> Unit = {},
    onShowToast: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by splashViewModel.collectAsStateWithLifecycle()
    val inAppUpdateCoordinator = remember(context.applicationContext.packageName) {
        PlayInAppUpdateCoordinator(context = context.applicationContext)
    }
    var updateUiState by rememberSaveable(stateSaver = GooglePlayUpdateStateSaver) {
        mutableStateOf(GooglePlayUpdateState.Checking)
    }

    fun requestCompleteFlexibleUpdateDialog() {
        if (updateUiState != GooglePlayUpdateState.ShowingFlexibleCompletionDialog) {
            updateUiState = GooglePlayUpdateState.ShowingFlexibleCompletionDialog
        }
    }

    LaunchedEffect(updateUiState) {
        when (updateUiState) {
            GooglePlayUpdateState.ImmediateUpdateFailed,
            GooglePlayUpdateState.ImmediateUpdateCanceled -> {
                onFinish()
            }

            GooglePlayUpdateState.ShowingFlexibleCompletionDialog -> {
                onShowDialog(
                    DialogData(
                        title = context.getString(R.string.in_app_update_complete_title),
                        message = context.getString(R.string.in_app_update_complete_message),
                        confirmText = context.getString(R.string.in_app_update_complete_confirm),
                        dismissText = context.getString(R.string.in_app_update_complete_dismiss),
                        dismissOnOutsideTouch = false,
                        dismissOnBackPress = false,
                        onConfirm = {
                            coroutineScope.launch {
                                val result = inAppUpdateCoordinator.completeFlexibleUpdate()
                                if (result.isFailure) {
                                    onShowToast(context.getString(R.string.in_app_update_failed))
                                }
                                updateUiState = GooglePlayUpdateState.ReadyToNavigate
                            }
                        },
                        onDismiss = {
                            updateUiState = GooglePlayUpdateState.ReadyToNavigate
                            onShowToast(context.getString(R.string.in_app_update_postponed))
                        }
                    )
                )
            }

            else -> Unit
        }
    }

    val inAppUpdateFlowLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // Play 업데이트 UI 화면이 어떻게 닫혔는지 받음
        when {
            //immediate 처리
            result.resultCode == Activity.RESULT_OK -> {
                if (updateUiState == GooglePlayUpdateState.WaitingImmediateResult) {
                    updateUiState = GooglePlayUpdateState.ReadyToNavigate
                }
            }

            updateUiState == GooglePlayUpdateState.WaitingImmediateResult -> {
                updateUiState = if (result.resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                    GooglePlayUpdateState.ImmediateUpdateFailed
                } else {
                    GooglePlayUpdateState.ImmediateUpdateCanceled
                }
            }

            //flexible 처리
            result.resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                updateUiState = GooglePlayUpdateState.ReadyToNavigate
                onShowToast(context.getString(R.string.in_app_update_failed))
            }

            else -> {
                updateUiState = GooglePlayUpdateState.ReadyToNavigate
                onShowToast(context.getString(R.string.in_app_update_postponed))
            }
        }
    }

    DisposableEffect(inAppUpdateCoordinator) {
        val flexibleInstallStateDecisionListener: (FlexibleInstallStateDecision) -> Unit = { decision ->
            // flexible 업데이트가 수락된 뒤 다운로드/설치 상태를 받음
            when (decision) {
                FlexibleInstallStateDecision.Downloaded -> {
                    requestCompleteFlexibleUpdateDialog()
                }

                FlexibleInstallStateDecision.Canceled -> {
                    updateUiState = GooglePlayUpdateState.ReadyToNavigate
                    onShowToast(context.getString(R.string.in_app_update_postponed))
                }

                FlexibleInstallStateDecision.Failed -> {
                    updateUiState = GooglePlayUpdateState.ReadyToNavigate
                    onShowToast(context.getString(R.string.in_app_update_failed))
                }
            }
        }

        inAppUpdateCoordinator.registerFlexibleInstallStateListener(flexibleInstallStateDecisionListener)
        onDispose {
            inAppUpdateCoordinator.unregisterFlexibleInstallStateListener()
        }
    }

    LaunchedEffect(Unit) {
        if (updateUiState != GooglePlayUpdateState.Checking) return@LaunchedEffect

        runCatching {
            inAppUpdateCoordinator.checkForUpdate(inAppUpdateFlowLauncher)
        }.onSuccess { decision ->
            handleInitialUpdateDecision(
                decision = decision,
                requestCompleteFlexibleUpdateDialog = ::requestCompleteFlexibleUpdateDialog,
                updateUiState = { updateUiState = it },
            )
        }.onFailure {
            updateUiState = GooglePlayUpdateState.ReadyToNavigate
            captureInAppUpdateCheckFailure(it)
            if (it is InstallException || it.cause is InstallException) //스토어 외의 경로로 설치 시 Install Error(-10) 발생
                onShowToast(context.getString(R.string.in_app_update_check_failed))
        }
    }

    LaunchedEffect(uiState.navigationDestination, updateUiState) {
        if (updateUiState != GooglePlayUpdateState.ReadyToNavigate) return@LaunchedEffect
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

private fun captureInAppUpdateCheckFailure(throwable: Throwable) {
    val installException = throwable.findInstallException()

    if (BuildConfig.DEBUG) return
    if (installException?.errorCode == -10) return

    //relate https://stackoverflow.com/questions/58637981/failed-to-bind-to-the-service
    if (throwable.hasMessage(PLAY_CORE_FAILED_TO_BIND_SERVICE_MESSAGE)) return

    Sentry.withScope { scope ->
        scope.setTag("feature", "in_app_update")
        scope.setTag("step", "check_for_update")
        Sentry.captureException(throwable)
    }
}

private fun Throwable.findInstallException(): InstallException? {
    var current: Throwable? = this
    while (current != null) {
        if (current is InstallException) return current
        current = current.cause
    }
    return null
}

private fun Throwable.hasMessage(message: String): Boolean {
    var current: Throwable? = this
    while (current != null) {
        if (current.message?.contains(message, ignoreCase = true) == true) return true
        current = current.cause
    }
    return false
}

private fun handleInitialUpdateDecision(
    decision: InitialInAppUpdateDecision,
    requestCompleteFlexibleUpdateDialog: () -> Unit,
    updateUiState: (GooglePlayUpdateState) -> Unit,
) {
    when (decision) {
        InitialInAppUpdateDecision.None -> {
            updateUiState(GooglePlayUpdateState.ReadyToNavigate)
        }

        is InitialInAppUpdateDecision.Flexible -> {
            updateUiState(GooglePlayUpdateState.WaitingFlexibleDownload)
        }

        is InitialInAppUpdateDecision.Immediate -> {
            updateUiState(GooglePlayUpdateState.WaitingImmediateResult)
        }

        InitialInAppUpdateDecision.CompleteFlexible -> {
            requestCompleteFlexibleUpdateDialog()
        }
    }
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
