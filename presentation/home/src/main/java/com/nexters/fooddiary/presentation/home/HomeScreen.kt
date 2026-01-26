package com.nexters.fooddiary.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun HomeScreen(
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_title),
                    fontSize = dimensionResource(R.dimen.home_title_text_size).value.sp
                )

                Button(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier
                        .padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.sign_out))
                }

                Button(
                    onClick = { showDeleteAccountDialog = true },
                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    Text(stringResource(R.string.delete_account))
                }
            }
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(stringResource(R.string.sign_out_confirm_title)) },
            text = { Text(stringResource(R.string.sign_out_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSignOutDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text(stringResource(R.string.delete_account_confirm_title)) },
            text = { Text(stringResource(R.string.delete_account_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAccountDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_title),
                    fontSize = dimensionResource(R.dimen.home_title_text_size).value.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = onNavigateToImage,
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "음식 분류하기",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
    }
}
