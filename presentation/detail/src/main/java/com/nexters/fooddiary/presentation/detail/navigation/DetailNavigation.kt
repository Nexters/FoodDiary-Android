package com.nexters.fooddiary.presentation.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.presentation.detail.DetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(
    val dateString: String,  // ISO-8601: "2026-01-16"
)

fun NavGraphBuilder.detailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToImagePicker: () -> Unit,
    onNavigateToModify: (String) -> Unit,
    onShowToast: (String) -> Unit,
) {
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()

        DetailScreen(
            initialDateString = route.dateString,
            onNavigateBack = onNavigateBack,
            onNavigateToImagePicker = onNavigateToImagePicker,
            onShowToast = onShowToast,
            onNavigateToModify = onNavigateToModify,
        )
    }
}
