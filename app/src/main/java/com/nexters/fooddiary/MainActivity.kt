package com.nexters.fooddiary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import com.nexters.fooddiary.core.ui.alert.AppDialogData
import com.nexters.fooddiary.core.ui.alert.DeleteAccountDialogData
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.core.ui.component.FoodDiaryDeleteAccountDialog
import com.nexters.fooddiary.core.ui.component.FoodDiaryDialog
import com.nexters.fooddiary.core.ui.component.FoodDiarySnackBar
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.navigation.FoodDiaryNavHost
import com.nexters.fooddiary.navigation.NavigationConstants
import com.nexters.fooddiary.push.FoodDiaryPushExtras
import com.nexters.fooddiary.push.FoodDiaryPushTypes
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var launchDeepLink: Uri? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchDeepLink = consumeDeepLink(intent)
        enableEdgeToEdge()
        setContent {
            val hazeState = rememberHazeState()
            var customSnackBarData by remember { mutableStateOf<SnackBarData?>(null) }
            var snackBarRequestId by remember { mutableStateOf(0) }
            var dialogData by remember { mutableStateOf<AppDialogData?>(null) }

            LaunchedEffect(snackBarRequestId) {
                if (snackBarRequestId == 0) return@LaunchedEffect
                val delayMillis = customSnackBarData?.delayMillis ?: 2_000L
                delay(delayMillis)
                customSnackBarData = null
            }

            FoodDiaryTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .navigationBarsPadding(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeSource(hazeState)
                    ) {
                        FoodDiaryNavHost(
                            initialDeepLink = launchDeepLink,
                            onFinish = { finish() },
                            onShowDialog = { data ->
                                dialogData = data
                            },
                            onShowSnackBar = { snackBarData ->
                                customSnackBarData = snackBarData
                                snackBarRequestId += 1
                            },
                            onShowToast = { message ->
                                customSnackBarData = SnackBarData(message = message)
                                snackBarRequestId += 1
                            }
                        )

                        dialogData?.let { data ->
                            when (data) {
                                is DialogData -> {
                                    FoodDiaryDialog(
                                        dialogData = data,
                                        onDismissRequest = { dialogData = null }
                                    )
                                }

                                is DeleteAccountDialogData -> {
                                    FoodDiaryDeleteAccountDialog(
                                        dialogData = data,
                                        onDismissRequest = { dialogData = null }
                                    )
                                }
                            }
                        }
                    }

                    customSnackBarData?.let { snackBarData ->
                        FoodDiarySnackBar(
                            message = snackBarData.message,
                            iconRes = snackBarData.iconRes,
                            hazeState = hazeState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchDeepLink = consumeDeepLink(intent)
    }

    private fun consumeDeepLink(intent: Intent?): Uri? {
        val directDeepLink = intent?.data
        if (directDeepLink != null) {
            intent.data = null
            return directDeepLink
        }

        val pushType = intent?.getStringExtra(FoodDiaryPushExtras.PUSH_TYPE).orEmpty()
        val pushDiaryDate = intent?.getStringExtra(FoodDiaryPushExtras.PUSH_DIARY_DATE).orEmpty()
        return when {
            pushType == FoodDiaryPushTypes.DAILY_RECORD_REMINDER -> {
                intent?.removeExtra(FoodDiaryPushExtras.PUSH_TYPE)
                Uri.Builder()
                    .scheme(NavigationConstants.DEEP_LINK_SCHEME)
                    .authority(NavigationConstants.DEEP_LINK_HOST_HOME)
                    .build()
            }

            pushType == FoodDiaryPushTypes.ANALYSIS_COMPLETE && pushDiaryDate.isNotBlank() -> {
                intent?.removeExtra(FoodDiaryPushExtras.PUSH_TYPE)
                intent?.removeExtra(FoodDiaryPushExtras.PUSH_DIARY_DATE)
                Uri.Builder()
                    .scheme(NavigationConstants.DEEP_LINK_SCHEME)
                    .authority(NavigationConstants.DEEP_LINK_HOST_DETAIL)
                    .appendQueryParameter(NavigationConstants.DEEP_LINK_QUERY_DATE, pushDiaryDate)
                    .build()
            }

            else -> null
        }
    }
}
