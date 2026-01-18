package com.nexters.fooddiary.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun HomeScreen(onNavigateToImage: () -> Unit) {
    val context = LocalContext.current
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = context.getString(R.string.home_title),
                    fontSize = context.resources.getDimension(R.dimen.home_title_text_size).sp,
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
}
