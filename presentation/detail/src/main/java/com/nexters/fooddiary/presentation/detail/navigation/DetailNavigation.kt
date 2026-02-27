package com.nexters.fooddiary.presentation.detail.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.core.common.push.PushSyncConstants
import com.nexters.fooddiary.presentation.detail.DetailScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class DetailRoute(
    val dateString: String,  // ISO-8601: "2026-01-16"
)

fun NavGraphBuilder.detailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToImagePicker: (LocalDate) -> Unit,
    onNavigateToModify: (String) -> Unit,
    onShowToast: (String) -> Unit,
) {
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()
        val pushSyncDateString by backStackEntry.savedStateHandle
            .getStateFlow<String?>(PushSyncConstants.PUSH_SYNC_DIARY_DATE, null)
            .collectAsStateWithLifecycle()

        DetailScreen(
            initialDateString = route.dateString,
            pushSyncDateString = pushSyncDateString,
            onPushSyncConsumed = {
                backStackEntry.savedStateHandle.remove<String>(PushSyncConstants.PUSH_SYNC_DIARY_DATE)
            },
            onNavigateBack = onNavigateBack,
            onNavigateToImagePicker = onNavigateToImagePicker,
            onNavigateToModify = onNavigateToModify,
            onShowToast = onShowToast,
        )
    }
}
