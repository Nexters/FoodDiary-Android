package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Sd700

private val ChipInactiveBg = Sd700
private val ChipActiveBg = PrimBase

@Composable
fun CommonChips(
    categories: Set<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            CategoryChip(
                label = category,
                isSelected = category == selectedCategory,
                onClick = { onSelect(category) },
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) ChipActiveBg else ChipInactiveBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = AppTypography.p14,
            color = if (isSelected) Gray050 else Gray400,
        )
    }
}
