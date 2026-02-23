package com.nexters.fooddiary.presentation.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.home.HomeScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToImagePicker: () -> Unit,
    onNavigateToDetail: (LocalDate) -> Unit,
    onNavigateToMyPage: () -> Unit,
    calendarToggleRequestId: () -> Int = { 0 },
    onShowSnackBar: (SnackBarData) -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToMyPage = onNavigateToMyPage,
            calendarToggleRequestId = calendarToggleRequestId(),
            onShowSnackBar = onShowSnackBar,
        )
    }
}
