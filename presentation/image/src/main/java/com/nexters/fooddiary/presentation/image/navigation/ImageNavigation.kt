package com.nexters.fooddiary.presentation.image.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.presentation.image.ImageClassificationScreen
import kotlinx.serialization.Serializable

@Serializable
object ImageRoute

fun NavGraphBuilder.imageScreen(
    onClose: () -> Unit
) {
    composable<ImageRoute> {
        ImageClassificationScreen(onClose = onClose)
    }
}

