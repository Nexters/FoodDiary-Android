package com.nexters.fooddiary.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    LaunchedEffect(initialDeepLink) {
        if (initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_CAMERA) {
            navController.navigate(CameraRoute)
        }
    }

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        homeScreen()
        cameraScreen(
            onClose = { navController.popBackStack() }
        )
    }
}

