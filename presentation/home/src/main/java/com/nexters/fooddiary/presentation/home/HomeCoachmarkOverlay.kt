package com.nexters.fooddiary.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.GlassmorphismStyle
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.glassmorphism
import dev.chrisbanes.haze.HazeState
import androidx.compose.ui.platform.LocalDensity
import com.nexters.fooddiary.core.ui.R as CoreUiR
import com.nexters.fooddiary.presentation.home.R as HomeR

private val CalendarMarkGlassStyle = GlassmorphismStyle(
    cornerRadius = 999.dp,
    borderWidth = 1.dp,
    blurRadius = 30.dp,
)

private val CalendarCoachmarkIconSize = 60.dp
private val CalendarCoachmarkIconGap = 12.dp
private val CalendarCoachmarkTextOffsetY = (-98).dp
private val WeeklyCoachmarkTopAdjustment = 10.dp

@Composable
internal fun HomeCoachmarkOverlay(
    onDismiss: () -> Unit,
    hazeState: HazeState?,
    weeklyHeaderBounds: Rect?,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.70f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss,
            ),
    ) {
        val density = LocalDensity.current
        val weeklyCoachmarkTop = weeklyHeaderBounds?.let { bounds ->
            with(density) { bounds.top.toDp() }
        } ?: (maxHeight * 0.269f)

        WeeklyMoveCoachmark(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = (weeklyCoachmarkTop + WeeklyCoachmarkTopAdjustment).coerceAtLeast(0.dp)),
        )
        CalendarToggleCoachmark(
            hazeState = hazeState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 64.dp),
        )
    }
}

@Composable
private fun WeeklyMoveCoachmark(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 29.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(HomeR.drawable.ic_weekly_coach_left),
                contentDescription = null,
                modifier = Modifier.size(width = 102.dp, height = 74.dp),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(HomeR.string.home_weekly_move_coachmark_message),
                    style = AppTypography.p12,
                    color = White,
                    textAlign = TextAlign.Left,
                )
            }
            Image(
                painter = painterResource(HomeR.drawable.ic_weekly_coach_right),
                contentDescription = null,
                modifier = Modifier.size(width = 101.dp, height = 74.dp),
            )
        }
    }
}

@Composable
private fun CalendarToggleCoachmark(
    hazeState: HazeState?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = stringResource(HomeR.string.home_calendar_switch_coachmark_message),
            style = AppTypography.p12,
            color = White,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(
                    x = -(CalendarCoachmarkIconSize),
                    y = CalendarCoachmarkTextOffsetY,
                ),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            verticalAlignment = Alignment.Bottom,
        ) {
            CalendarModeMark(
                iconRes = CoreUiR.drawable.ic_weekly_calendar,
                label = stringResource(HomeR.string.home_weekly_label),
                hazeState = hazeState,
            )
            Spacer(modifier = Modifier.width(CalendarCoachmarkIconGap))
            CalendarModeMark(
                iconRes = CoreUiR.drawable.ic_monthly_calendar,
                label = stringResource(HomeR.string.home_monthly_label),
                hazeState = hazeState,
                coachArrowRes = HomeR.drawable.ic_calendar_coach_left,
                coachArrowOffsetX = (-6).dp,
            )
        }

    }
}

@Composable
private fun CalendarModeMark(
    iconRes: Int,
    label: String,
    hazeState: HazeState?,
    coachArrowRes: Int? = null,
    coachArrowOffsetX: Dp = 0.dp,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(if (coachArrowRes != null) 84.dp else 60.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (coachArrowRes != null) {
                Image(
                    painter = painterResource(coachArrowRes),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = coachArrowOffsetX)
                        .size(width = 16.dp, height = 31.dp),
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = if (coachArrowRes != null) 24.dp else 0.dp)
                    .size(60.dp)
                    .glassmorphism(
                        hazeState = hazeState,
                        style = CalendarMarkGlassStyle,
                    )
                    .border(2.dp, White.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(34.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            style = AppTypography.p12,
            color = White,
        )
    }
}
