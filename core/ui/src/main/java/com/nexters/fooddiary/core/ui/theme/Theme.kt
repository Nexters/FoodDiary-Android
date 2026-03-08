package com.nexters.fooddiary.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FoodDiaryColorScheme = lightColorScheme(
    primary = PrimBase,
    onPrimary = White,
//    secondary = ComBase,
    onSecondary = White,
//    tertiary = PersBase,
    onTertiary = White,
    error = RnBase,
    onError = White,
    background = GrayBase,
    onBackground = White,
    surface = GrayBase,
    onSurface = White,
)

@Composable
fun FoodDiaryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FoodDiaryColorScheme,
        typography = Typography,
        content = content
    )
}

