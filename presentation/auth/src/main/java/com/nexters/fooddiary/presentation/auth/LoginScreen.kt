package com.nexters.fooddiary.presentation.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.theme.Gray540
import com.nexters.fooddiary.presentation.auth.R as AuthR
import com.nexters.fooddiary.core.ui.R as CoreR

private val LoginBackgroundColor = Color(0xFF191821)
private val GoogleButtonColor = Color.White

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = mavericksViewModel(),
    onAuthStateChange: (AuthUiState) -> Unit = {},
    deleteAccountRequestId: Int = 0,
) {
    val context = LocalContext.current
    val authUiState by authViewModel.collectAsStateWithLifecycle()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { resultIntent ->
            authViewModel.processSignInResult(context, resultIntent)
        }
    }

    LaunchedEffect(authUiState) {
        onAuthStateChange(authUiState)
    }

    LaunchedEffect(deleteAccountRequestId) {
        if (deleteAccountRequestId > 0) {
            authViewModel.deleteAccount()
        }
    }

    LaunchedEffect(authUiState.signInIntent) {
        authUiState.signInIntent?.let { intent ->
            googleSignInLauncher.launch(intent)
            authViewModel.consumeSignInIntent()
        }
    }

    LoginScreen(
        modifier = modifier,
        uiState = authUiState,
        onGoogleSignInClick = { authViewModel.startGoogleSignIn(context) },
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: AuthUiState = AuthUiState(),
    onGoogleSignInClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LoginBackgroundColor)
            .navigationBarsPadding()
    ) {
        // 중앙 고정 - 아이콘
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = CoreR.drawable.img_app_title_text),
                contentDescription = stringResource(id = AuthR.string.app_title),
                modifier = Modifier
                    .width(215.dp)
                    .height(58.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = CoreR.drawable.img_app_title_image),
                contentDescription = stringResource(id = AuthR.string.app_mascot_description),
                modifier = Modifier.size(180.dp)
            )
        }

        // 최하단 고정 - 버튼/로딩
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 32.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                // Google Sign In Button
                Button(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoogleButtonColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = AuthR.drawable.ic_google_logo),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = AuthR.string.sign_in_with_google),
                            fontFamily = FontFamily.Default,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gray540,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        uiState = AuthUiState(),
        onGoogleSignInClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    LoginScreen(
        uiState = AuthUiState(isLoading = true),
        onGoogleSignInClick = {},
    )
}
