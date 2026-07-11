package com.nexters.fooddiary.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.neonShadow
import dev.chrisbanes.haze.HazeState

internal enum class HomeInsightTab {
    HOME,
    INSIGHT,
}

private val BottomBarNeonBackgroundBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFFE670E), Color(0xFFFF853D))
)
private val BottomBarNeonBorderBrush = Brush.verticalGradient(
    colors = listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)
)

@Composable
internal fun HomeInsightBottomBar(
    selectedTab: HomeInsightTab,
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        HomeInsightToggle(
            selectedTab = selectedTab,
            onHomeClick = onHomeClick,
            onInsightClick = onInsightClick,
            hazeState = hazeState,
        )
    }
}

@Composable
private fun HomeInsightToggle(
    selectedTab: HomeInsightTab,
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier,
) {
    val isHomeSelected = selectedTab == HomeInsightTab.HOME
    val isInsightSelected = selectedTab == HomeInsightTab.INSIGHT

    Row(
        modifier = modifier
            .height(60.dp)
            .border(width = 1.dp, color = Color.Black, shape = CircleShape)
            .background(color = Color.White, shape = CircleShape)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .height(44.dp)
                .width(75.dp)
                .selectionBackground(isHomeSelected)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onHomeClick,
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(drawable.ic_home),
                contentDescription = stringResource(string.home_nav_home),
                tint = if (isHomeSelected) White else Gray600,
                modifier = Modifier.size(20.dp),
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(string.home_nav_home),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isHomeSelected) White else Gray600,
            )
        }
        Row(
            modifier = Modifier
                .height(44.dp)
                .width(105.dp)
                .selectionBackground(isInsightSelected)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onInsightClick,
                )
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(drawable.ic_insights),
                contentDescription = stringResource(string.home_nav_insight),
                tint = if (isInsightSelected) White else Gray600,
                modifier = Modifier.size(20.dp),
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(string.home_nav_insight),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isInsightSelected) White else Gray600,
            )
        }
    }
}

private fun Modifier.selectionBackground(isSelected: Boolean): Modifier {
    return if (isSelected) {
        this.border(width = 1.dp, brush = BottomBarNeonBorderBrush, shape = CircleShape)
            .background(brush = BottomBarNeonBackgroundBrush, shape = CircleShape)
    } else {
        this
            .background(color = Color.White.copy(alpha = 0.92f), shape = CircleShape)
    }
}
