package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.fooddiary.core.ui.R
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase

@Composable
fun DetailScreenHeader(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
            .padding(vertical = 16.dp)
            .background(color = SdBase),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackButtonClick,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "back_button",
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
@Preview
fun DetailScreenHeaderPreview() {
    DetailScreenHeader(
        onBackButtonClick = {},
    ) {
        Text (
            text = "내 정보",
            color = Gray050,
        )
    }
}

