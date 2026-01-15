package com.nexters.fooddiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.navigation.FoodDiaryNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodDiaryTheme {
                FoodDiaryNavHost(
                    initialDeepLink = intent?.data,
                    onFinish = { finish() }
                )
            }
        }
    }
}