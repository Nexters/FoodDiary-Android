package com.nexters.fooddiary.presentation.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.presentation.webview.WebViewScreen
import kotlinx.serialization.Serializable

@Serializable
data class WebViewRoute(
    val url: String
)

fun NavGraphBuilder.webViewScreen(
    onClose: () -> Unit
) {
    composable<WebViewRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WebViewRoute>()
        WebViewScreen(
            url = route.url,
            onClose = onClose
        )
    }
}
