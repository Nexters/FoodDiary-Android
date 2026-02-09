package com.nexters.fooddiary.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel

@Composable
internal fun SplashScreen(
    modifier: Modifier = Modifier,
    splashViewModel: SplashViewModel = mavericksViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val uiState by splashViewModel.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigationDestination) {
        uiState.navigationDestination?.let { destination ->
            when (destination) {
                NavigationDestination.Home -> onNavigateToHome()
                NavigationDestination.Login -> onNavigateToLogin()
            }
            splashViewModel.consumeNavigation()
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF191821)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "스플래시",
            fontSize = 36.sp,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashContentPreview() {
    SplashContent()
}
