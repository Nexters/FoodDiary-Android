package com.nexters.fooddiary.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.R as CoreR

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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = CoreR.drawable.ic_app_main_logo_text),
                contentDescription = null,
                modifier = Modifier
                    .width(225.dp)
                    .height(82.dp),
            )

            Image(
                painter = painterResource(id = CoreR.drawable.img_app_title_image),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashContentPreview() {
    SplashContent()
}
