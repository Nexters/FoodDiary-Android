package com.nexters.fooddiary.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nexters.fooddiary.presentation.camera.navigation.CameraRoute
import com.nexters.fooddiary.presentation.camera.navigation.cameraScreen
import com.nexters.fooddiary.presentation.home.navigation.HomeRoute
import com.nexters.fooddiary.presentation.home.navigation.homeScreen

@Composable
fun FoodDiaryNavHost(
    initialDeepLink: Uri? = null,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_CAMERA) {
        CameraRoute
    } else {
        HomeRoute
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeScreen(
            onNavigateToCamera = { navController.navigate(CameraRoute) }
        )
        cameraScreen(
            onClose = { navController.popBackStack() }
        )
    }
}

