package com.nexters.fooddiary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.navigation.FoodDiaryNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            FoodDiaryTheme {
                FoodDiaryNavHost(
                    initialDeepLink = intent?.data,
                    onFinish = { finish() },
                    onShowDialog = { dialogData ->
                        // TODO: Dialog 구현
                    },
                    onShowSnackBar = { snackBarData ->
                        // TODO: SnackBar 구현
                    },
                    onShowToast = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}
