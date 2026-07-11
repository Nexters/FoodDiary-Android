package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.theme.AppBackground
import com.nexters.fooddiary.core.ui.theme.AppSurfaceOverlay
import com.nexters.fooddiary.core.ui.theme.AppSurfaceVariant
import com.nexters.fooddiary.core.ui.theme.AppTextPrimary
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray300
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.White

private val KeywordChipShape = RoundedCornerShape(999.dp)

@Composable
fun KeywordChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    selectedContainerColor: Color = AppTextPrimary,
    selectedContentColor: Color = White,
    unselectedContainerColor: Color = AppSurfaceVariant,
    unselectedContentColor: Color = Gray600,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val containerColor = if (selected) selectedContainerColor else unselectedContainerColor
    val contentColor = if (selected) selectedContentColor else unselectedContentColor

    Row(
        modifier = modifier
            .background(color = containerColor, shape = KeywordChipShape)
            .let { base ->
                if (onClick == null) base else base.clickable(onClick = onClick)
            }
            .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = text,
            style = AppTypography.p14,
            color = contentColor,
        )
        trailingContent?.invoke()
    }
}

@Composable
fun KeywordChipGroup(
    keywords: Collection<String>,
    modifier: Modifier = Modifier,
    selectedKeywords: Set<String> = emptySet(),
    onKeywordClick: ((String) -> Unit)? = null,
    horizontalSpacing: Int = 8,
    verticalSpacing: Int = 8,
    selectedContainerColor: Color = AppTextPrimary,
    selectedContentColor: Color = White,
    unselectedContainerColor: Color = AppSurfaceVariant,
    unselectedContentColor: Color = Gray600,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing.dp),
    ) {
        keywords.forEach { keyword ->
            key(keyword) {
                KeywordChip(
                    text = keyword,
                    selected = selectedKeywords.contains(keyword),
                    onClick = onKeywordClick?.let { click -> { click(keyword) } },
                    selectedContainerColor = selectedContainerColor,
                    selectedContentColor = selectedContentColor,
                    unselectedContainerColor = unselectedContainerColor,
                    unselectedContentColor = unselectedContentColor,
                )
            }
        }
    }
}

@Composable
fun EditableKeywordChipGroup(
    keywords: List<String>,
    onAddClick: () -> Unit,
    onKeywordRemove: (String) -> Unit,
    removeContentDescription: String,
    addContentDescription: String,
    modifier: Modifier = Modifier,
    horizontalSpacing: Int = 8,
    verticalSpacing: Int = 4,
) {
    val removePainter = painterResource(drawable.ic_circle_close)

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing.dp),
    ) {
        keywords.forEach { keyword ->
            key(keyword) {
                KeywordChip(
                    text = keyword,
                    trailingContent = {
                        Image(
                            painter = removePainter,
                            contentDescription = removeContentDescription,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onKeywordRemove(keyword) },
                        )
                    },
                )
            }
        }
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PrimBase)
                .clickable(onClick = onAddClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = addContentDescription,
                tint = Gray050,
            )
        }
    }
}

@Composable
fun TasteKeywordSection(
    title: String,
    keywords: List<String>,
    modifier: Modifier = Modifier,
    selectedKeywords: Set<String> = emptySet(),
    onKeywordClick: ((String) -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .background(
                color = AppSurfaceOverlay,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Text(
                text = title,
                style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                color = AppTextPrimary,
            )
            KeywordChipGroup(
                keywords = keywords,
                selectedKeywords = selectedKeywords,
                onKeywordClick = onKeywordClick,
                horizontalSpacing = 8,
                verticalSpacing = 8,
                unselectedContainerColor = AppSurfaceVariant,
                unselectedContentColor = Gray400,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TasteKeywordSectionPreview() {
    Row(
        modifier = Modifier
            .background(AppBackground)
            .padding(16.dp),
    ) {
        TasteKeywordSection(
            title = "나의 입맛과\n가장 잘 어울리는 키워드",
            keywords = listOf("#집밥", "#중식", "#튀김", "#소면"),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KeywordChipGroupSelectedPreview() {
    KeywordChipGroup(
        keywords = listOf("한식", "일식", "중식", "양식"),
        selectedKeywords = setOf("중식"),
        unselectedContentColor = Gray300,
    )
}
