package com.nexters.fooddiary.presentation.auth

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onAuthStateChange: (AuthUiState) -> Unit = {},
    signOutRequestId: Int = 0,
    deleteAccountRequestId: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authUiState by authViewModel.state.collectAsState()

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

    LaunchedEffect(signOutRequestId) {
        if (signOutRequestId > 0) {
            authViewModel.signOut()
        }
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


    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Food Diary",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "식단을 기록하고 관리하세요",
                style = MaterialTheme.typography.bodyLarge
            )

            if (authUiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { authViewModel.startGoogleSignIn(context) },
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text("Google로 로그인")
                }
            }
        }
    }
}
