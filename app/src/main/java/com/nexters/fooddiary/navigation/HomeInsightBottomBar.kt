package com.nexters.fooddiary.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.theme.GlassmorphismStyle
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.glassmorphism
import dev.chrisbanes.haze.HazeState

internal enum class HomeInsightTab {
    HOME,
    INSIGHT,
}

private val BottomBarGlassStyle = GlassmorphismStyle(
    cornerRadius = 999.dp,
    blurRadius = 30.dp,
)

@Composable
internal fun HomeInsightBottomBar(
    selectedTab: HomeInsightTab,
    isMonthlyCalendarView: Boolean,
    showCalendarToggle: Boolean,
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit,
    onCalendarViewToggle: () -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HomeInsightToggle(
            selectedTab = selectedTab,
            onHomeClick = onHomeClick,
            onInsightClick = onInsightClick,
            hazeState = hazeState,
        )
        if (showCalendarToggle) {
            IconButton(
                modifier = Modifier
                    .size(60.dp)
                    .glassmorphism(
                        hazeState = hazeState,
                        style = BottomBarGlassStyle,
                    ),
                onClick = onCalendarViewToggle,
                shape = CircleShape,
                colors = remember {
                    IconButtonColors(
                        containerColor = Transparent,
                        contentColor = Gray050,
                        disabledContainerColor = Transparent,
                        disabledContentColor = Gray050,
                    )
                },
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isMonthlyCalendarView) drawable.ic_weekly_calendar else drawable.ic_monthly_calendar
                    ),
                    contentDescription = stringResource(string.calendar),
                    tint = Gray050,
                )
            }
        } else {
            Spacer(modifier = Modifier.size(60.dp))
        }
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
            .glassmorphism(
                hazeState = hazeState,
                style = BottomBarGlassStyle,
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .height(44.dp)
                .width(75.dp)
                .clip(CircleShape)
                .background(if (isHomeSelected) PrimBase else Transparent)
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
                tint = if (isHomeSelected) White else Gray050,
                modifier = Modifier.size(20.dp),
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(string.home_nav_home),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isHomeSelected) White else Gray050,
            )
        }
        Row(
            modifier = Modifier
                .height(44.dp)
                .width(105.dp)
                .clip(CircleShape)
                .background(if (isInsightSelected) PrimBase else Transparent)
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
                tint = if (isInsightSelected) White else Gray050,
                modifier = Modifier.size(20.dp),
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(string.home_nav_insight),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isInsightSelected) White else Gray050,
            )
        }
    }
}
