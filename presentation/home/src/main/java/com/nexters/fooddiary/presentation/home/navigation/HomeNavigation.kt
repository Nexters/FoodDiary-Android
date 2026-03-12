package com.nexters.fooddiary.presentation.home.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.core.common.navigation.SyncConstants
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
        val refreshDiaryDateString by backStackEntry.savedStateHandle
            .getStateFlow<String?>(SyncConstants.DIARY_REFRESH_DATE, null)
            .collectAsState()
        val diaryUploadPendingDateString by backStackEntry.savedStateHandle
            .getStateFlow<String?>(SyncConstants.DIARY_UPLOAD_PENDING_DATE, null)
            .collectAsState()
        HomeScreen(
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToMyPage = onNavigateToMyPage,
            isMonthlyCalendarView = isMonthlyCalendarView(),
            refreshDiaryDateString = refreshDiaryDateString,
            diaryUploadPendingDateString = diaryUploadPendingDateString,
            onRefreshDiaryConsumed = {
                backStackEntry.savedStateHandle.remove<String>(SyncConstants.DIARY_REFRESH_DATE)
            },
            onDiaryUploadPendingConsumed = {
                backStackEntry.savedStateHandle.remove<String>(SyncConstants.DIARY_UPLOAD_PENDING_DATE)
            },
            onShowSnackBar = onShowSnackBar,
        )
    }
}
