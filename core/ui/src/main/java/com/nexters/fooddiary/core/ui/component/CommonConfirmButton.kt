package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.PrimBase

object FoodDiaryButtonDefaults {
    val PrimaryColors: ButtonColors
        @Composable
        get() = ButtonDefaults.buttonColors(
            contentColor = PrimBase,
            containerColor = PrimBase,
        )
}

@Composable
fun CommonCircleButton(
    modifier: Modifier = Modifier,
    buttonColors: ButtonColors = FoodDiaryButtonDefaults.PrimaryColors,
    buttonText: String = "",
    border: BorderStroke? = null,
    contentColor: Color? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = buttonColors,
        border = border,
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        Text(
            text = buttonText,
            style = AppTypography.hd15,
            color = contentColor ?: Gray050,
        )
    }
}
