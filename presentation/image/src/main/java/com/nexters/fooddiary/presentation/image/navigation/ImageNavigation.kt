package com.nexters.fooddiary.presentation.image.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.presentation.image.ImagePickerScreen
import kotlinx.serialization.Serializable

@Serializable
data class ImagePickerRoute(
    val dateString: String? = null
)

fun NavGraphBuilder.imageScreen(
    onClose: () -> Unit
) {
    composable<ImagePickerRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ImagePickerRoute>()
        ImagePickerScreen(
            selectedDateString = route.dateString,
            onClose = onClose,
        )
    }
}

