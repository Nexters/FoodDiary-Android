package com.nexters.fooddiary.presentation.modify

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray300
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.White

@Composable
internal fun CommonCircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    buttonText: String,
    contentColor: Color = White,
    border: BorderStroke? = null,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        border = border,
        colors = buttonColors,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = buttonText,
            style = AppTypography.p14.copy(fontWeight = FontWeight.Medium),
            color = contentColor,
        )
    }
}

@Composable
internal fun CommonChips(
    categories: Set<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            val bgColor = if (isSelected) Gray050 else Sd800
            val textColor = if (isSelected) Color.Black else Gray300
            Row(
                modifier = Modifier
                    .background(bgColor, RoundedCornerShape(999.dp))
                    .clickable { onSelect(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = category,
                    style = AppTypography.p14,
                    color = textColor,
                )
            }
        }
    }
}
