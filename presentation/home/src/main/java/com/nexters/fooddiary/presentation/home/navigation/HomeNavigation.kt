package com.nexters.fooddiary.presentation.home.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.core.common.navigation.SyncConstants
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.home.HomeEntryScreen
import com.nexters.fooddiary.presentation.home.HomePermissionGuideScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object HomeRoute

@Serializable
object HomePermissionGuideRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToImagePicker: (LocalDate) -> Unit,
    onNavigateToDetail: (LocalDate) -> Unit,
    onNavigateToPermissionGuide: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    isMonthlyCalendarView: () -> Boolean = { false },
    onShowSnackBar: (SnackBarData) -> Unit,
) {
    composable<HomeRoute> { backStackEntry ->
        val refreshDiaryDateString by backStackEntry.savedStateHandle
            .getStateFlow<String?>(SyncConstants.DIARY_REFRESH_DATE, null)
            .collectAsState()
        HomeEntryScreen(
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToPermissionGuide = onNavigateToPermissionGuide,
            onNavigateToMyPage = onNavigateToMyPage,
            isMonthlyCalendarView = isMonthlyCalendarView(),
            refreshDiaryDateString = refreshDiaryDateString,
            onRefreshDiaryConsumed = {
                backStackEntry.savedStateHandle.remove<String>(SyncConstants.DIARY_REFRESH_DATE)
            },
            onShowSnackBar = onShowSnackBar,
        )
    }
}

fun NavGraphBuilder.homePermissionGuideScreen(
    onOpenSettings: () -> Unit,
    onPermissionGranted: () -> Unit,
) {
    composable<HomePermissionGuideRoute> {
        HomePermissionGuideScreen(
            onOpenSettings = onOpenSettings,
            onPermissionGranted = onPermissionGranted,
        )
    }
}
