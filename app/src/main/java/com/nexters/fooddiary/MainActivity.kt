package com.nexters.fooddiary

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.nexters.fooddiary.core.ui.component.FoodDiarySnackBar
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.navigation.FoodDiaryNavHost
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            var customSnackBarData by remember { mutableStateOf<SnackBarData?>(null) }

            @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
            FoodDiaryTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            FoodDiarySnackBar(
                                message = data.visuals.message,
                                iconRes = customSnackBarData?.iconRes
                            )
                        }
                    }
                ) { innerPadding ->
                    FoodDiaryNavHost(
                        initialDeepLink = intent?.data,
                        onFinish = { finish() },
                        onShowDialog = { dialogData ->
                            // TODO: Dialog 구현
                        },
                        onShowSnackBar = { snackBarData ->
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = snackBarData.message,
                                    actionLabel = snackBarData.actionLabel,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    snackBarData.onAction?.invoke()
                                }
                            }
                        },
                        onShowToast = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
}
