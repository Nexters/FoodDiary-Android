package com.nexters.fooddiary.presentation.modify.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.presentation.modify.ModifyScreen
import kotlinx.serialization.Serializable

@Serializable
data class ModifyRoute(val diaryId: String)

fun NavGraphBuilder.modifyScreen(
    onBack: () -> Unit,
    onNavigateToImagePicker: () -> Unit = {},
    onShowDialog: (DialogData) -> Unit = {},
) {
    composable<ModifyRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ModifyRoute>()
        ModifyScreen(
            diaryId = route.diaryId,
            onBack = onBack,
            onNavigateToImagePicker = onNavigateToImagePicker,
            onShowDialog = onShowDialog,
        )
    }
}
