package com.nexters.fooddiary

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.core.ui.component.FoodDiaryDialog
import com.nexters.fooddiary.core.ui.component.FoodDiarySnackBar
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.navigation.FoodDiaryNavHost
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val hazeState = rememberHazeState()
            var customSnackBarData by remember { mutableStateOf<SnackBarData?>(null) }
            var snackBarRequestId by remember { mutableStateOf(0) }
            var dialogData by remember { mutableStateOf<DialogData?>(null) }

            LaunchedEffect(snackBarRequestId) {
                if (snackBarRequestId == 0) return@LaunchedEffect
                val delayMillis = customSnackBarData?.delayMillis ?: 2_000L
                delay(delayMillis)
                customSnackBarData = null
            }

            FoodDiaryTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeSource(hazeState)
                    ) {
                        FoodDiaryNavHost(
                            initialDeepLink = intent?.data,
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
                            FoodDiaryDialog(
                                dialogData = data,
                                onDismissRequest = { dialogData = null }
                            )
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
}
