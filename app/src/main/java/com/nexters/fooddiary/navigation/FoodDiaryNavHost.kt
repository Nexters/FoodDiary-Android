package com.nexters.fooddiary.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nexters.fooddiary.presentation.image.navigation.ImageRoute
import com.nexters.fooddiary.presentation.image.navigation.imageScreen
import com.nexters.fooddiary.presentation.home.navigation.HomeRoute
import com.nexters.fooddiary.presentation.home.navigation.homeScreen

@Composable
fun FoodDiaryNavHost(
    initialDeepLink: Uri? = null,
    onFinish: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (initialDeepLink?.host == NavigationConstants.DEEP_LINK_HOST_IMAGE) {
        ImageRoute
    } else {
        HomeRoute
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeScreen(
            onNavigateToImage = { navController.navigate(ImageRoute) }
        )
        imageScreen(
            onClose = {
                if (!navController.popBackStack()) {
                    onFinish()
                }
            }
        )
    }
}

