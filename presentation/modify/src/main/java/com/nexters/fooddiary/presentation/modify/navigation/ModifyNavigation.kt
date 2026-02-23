package com.nexters.fooddiary.presentation.modify.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.modify.ModifyScreen
import kotlinx.serialization.Serializable

@Serializable
data class ModifyRoute(
    val diaryId: String,
    val dateString: String? = null,
)

fun NavGraphBuilder.modifyScreen(
    onBack: () -> Unit,
    onNavigateToImagePicker: (dateString: String?) -> Unit = {},
    onShowDialog: (DialogData) -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
) {
    composable<ModifyRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ModifyRoute>()
        ModifyScreen(
            diaryId = route.diaryId,
            onBack = onBack,
            onNavigateToImagePicker = { onNavigateToImagePicker(route.dateString) },
            onShowDialog = onShowDialog,
            onShowSnackBar = onShowSnackBar,
        )
    }
}
