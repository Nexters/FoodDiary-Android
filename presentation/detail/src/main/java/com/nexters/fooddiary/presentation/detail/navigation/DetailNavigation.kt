package com.nexters.fooddiary.presentation.detail.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.core.common.navigation.SyncConstants
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.detail.DetailScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class DetailRoute(
    val dateString: String,  // ISO-8601: "2026-01-16"
)

fun NavGraphBuilder.detailScreen(
    onDeleteSuccess: (LocalDate) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToImagePicker: (LocalDate) -> Unit,
    onNavigateToModify: (String) -> Unit,
    onShowSnackBar: (SnackBarData) -> Unit,
    onShowToast: (String) -> Unit,
) {
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()
        val refreshDiaryDateString by backStackEntry.savedStateHandle
            .getStateFlow<String?>(SyncConstants.DIARY_REFRESH_DATE, null)
            .collectAsStateWithLifecycle()

        DetailScreen(
            initialDateString = route.dateString,
            refreshDiaryDateString = refreshDiaryDateString,
            onRefreshDiaryConsumed = {
                backStackEntry.savedStateHandle.remove<String>(SyncConstants.DIARY_REFRESH_DATE)
            },
            onDeleteSuccess = onDeleteSuccess,
            onNavigateBack = onNavigateBack,
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToModify = onNavigateToModify,
            onShowSnackBar = onShowSnackBar,
            onShowToast = onShowToast,
        )
    }
}
