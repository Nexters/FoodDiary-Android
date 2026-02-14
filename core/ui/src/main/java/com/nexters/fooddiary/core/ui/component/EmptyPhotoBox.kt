package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray900
import com.nexters.fooddiary.core.ui.theme.White

@Composable
fun AddPhotoBox(
    modifier: Modifier = Modifier,
    onAddPhoto: () -> Unit,
) {
    Box(
        modifier = modifier
            .heightIn(min = 200.dp)
            .background(color = White.copy(alpha = 0.02f), shape = RoundedCornerShape(16.dp))
            .graphicsLayer { clip = false }
            .drawBehind {
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = 0f,
                            top = 0f,
                            right = size.width,
                            bottom = size.height,
                            cornerRadius = CornerRadius(16.dp.toPx()),
                        )
                    )
                }
                drawPath(
                    path = path,
                    color = Gray900,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(3.dp.toPx(), 3.dp.toPx()),
                            phase = 0f,
                        ),
                    ),
                )
            }
            .clickable(onClick = onAddPhoto),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                modifier = modifier.size(180.dp),
                painter = painterResource(drawable.ic_add_diary),
                contentDescription = null,
            )
            Text(
                text = stringResource(string.home_add_food_photo),
                style = AppTypography.p12,
                color = White,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun AddPhotoBoxPreview(){
    AddPhotoBox(
        onAddPhoto = {}
    )
}