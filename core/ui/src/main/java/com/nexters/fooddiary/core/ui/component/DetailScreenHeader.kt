package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.R
import com.nexters.fooddiary.core.ui.theme.AppDivider
import com.nexters.fooddiary.core.ui.theme.AppTextPrimary
import com.nexters.fooddiary.core.ui.theme.AppBackground

@Composable
fun DetailScreenHeader(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit = {},
    content : @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
            .background(color = AppBackground)
            .drawBehind {
                drawLine(
                    color = AppDivider,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackButtonClick,
            modifier = Modifier.size(32.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                colorFilter = ColorFilter.tint(Color.Black),
                contentDescription = "back_button",
            )
        }
        content()
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
            color = AppTextPrimary,
        )
    }
}
