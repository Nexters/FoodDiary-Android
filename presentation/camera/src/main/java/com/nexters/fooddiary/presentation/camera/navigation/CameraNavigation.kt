package com.nexters.fooddiary.presentation.camera.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.camera.CameraScreen
import kotlinx.serialization.Serializable

@Serializable
object CameraRoute

fun NavGraphBuilder.cameraScreen(
    onClose: () -> Unit
) {
    composable<CameraRoute> {
        CameraScreen(onClose = onClose)
    }
}

