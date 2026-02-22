package com.nexters.fooddiary.presentation.image.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.image.ImagePickerScreen
import kotlinx.serialization.Serializable

@Serializable
object ImagePickerRoute

fun NavGraphBuilder.imageScreen(
    onClose: () -> Unit
) {
    composable<ImagePickerRoute> {
        ImagePickerScreen(
            onClose = onClose,
        )
    }
}

