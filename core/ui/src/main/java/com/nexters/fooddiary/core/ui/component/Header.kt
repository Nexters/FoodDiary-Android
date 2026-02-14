package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.R

private const val HeaderHeightDp = 32

@Composable
fun Header(
    modifier: Modifier = Modifier,
    onClickMyPage: () -> Unit = { },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HeaderHeightDp.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier.height(HeaderHeightDp.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_txt_logo),
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = "Text Logo",
            )
        }
        Box(
            modifier = Modifier
                .size(HeaderHeightDp.dp)
                .clickable(onClick = onClickMyPage),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_my_page),
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = "Mypage Icon",
            )
        }
    }
}

@Composable
@Preview
fun HeaderPreview(background: Boolean = true) {
    Header()
}