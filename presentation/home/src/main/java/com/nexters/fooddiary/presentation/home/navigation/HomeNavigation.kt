package com.nexters.fooddiary.presentation.home.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.core.common.push.PushSyncConstants
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.home.HomeScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToImagePicker: (LocalDate) -> Unit,
    onNavigateToDetail: (LocalDate) -> Unit,
    onNavigateToMyPage: () -> Unit,
    isMonthlyCalendarView: () -> Boolean = { false },
    onShowSnackBar: (SnackBarData) -> Unit,
) {
    composable<HomeRoute> { backStackEntry ->
        val pushSyncDateString by backStackEntry.savedStateHandle
            .getStateFlow<String?>(PushSyncConstants.PUSH_SYNC_DIARY_DATE, null)
            .collectAsState()
        HomeScreen(
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToMyPage = onNavigateToMyPage,
            isMonthlyCalendarView = isMonthlyCalendarView(),
            pushSyncDateString = pushSyncDateString,
            onPushSyncConsumed = {
                backStackEntry.savedStateHandle.remove<String>(PushSyncConstants.PUSH_SYNC_DIARY_DATE)
            },
            onShowSnackBar = onShowSnackBar,
        )
    }
}
