package com.nexters.fooddiary.presentation.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToImagePicker: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onShowSnackBar: (SnackBarData) -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToMyPage = onNavigateToMyPage,
            onShowSnackBar = onShowSnackBar,
        )
    }
}
