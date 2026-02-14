package com.nexters.fooddiary.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.calendar.MonthlyCalendar
import com.nexters.fooddiary.core.ui.calendar.rememberMonthCalendarState
import com.nexters.fooddiary.core.ui.calendar.rememberWeeklyCalendarState
import com.nexters.fooddiary.core.ui.component.AddPhotoBox
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.gradientBorder
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray750
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.calendar.WeeklyCalendar
import java.time.LocalDate
import androidx.compose.material3.Button
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private val ToggleCalendarStrokeGradient = Brush.linearGradient(
    *arrayOf(
        0f to White.copy(alpha = 0.10f),
        1f to White.copy(alpha = 0f),
    ),
    start = Offset(0f, 0f),
    end = Offset(60f, 60f),
)

private val SelectedTabStrokeGradient = Brush.linearGradient(
    *arrayOf(
        0f to White.copy(alpha = 0.11f),
        0.54f to White.copy(alpha = 0f),
        1f to White.copy(alpha = 0.05f),
    ),
    start = Offset(0f, 0f),
    end = Offset(1000f, 1000f),
)


private fun Modifier.selectedTabGradientBorder(selected: Boolean) =
    then(if (selected) Modifier.gradientBorder(1.dp, SelectedTabStrokeGradient, CircleShape) else Modifier)

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToImagePicker: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    viewModel: HomeViewModel = mavericksViewModel(),
) {
    val state by viewModel.collectAsState()
    val photoCountByDate by viewModel.photoCountByDate.collectAsState(initial = emptyMap())

    HomeScreen(
        state = state,
        photoCountByDate = photoCountByDate,
        onDateSelected = viewModel::onDateSelected,
        onToggleCalendarView = viewModel::onToggleCalendarView,
        onNavigateToImagePicker = onNavigateToImagePicker,
        onNavigateToMyPage = onNavigateToMyPage,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeScreenState = HomeScreenState(),
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
    onDateSelected: (LocalDate) -> Unit = {},
    onToggleCalendarView: () -> Unit = {},
    onNavigateToImagePicker: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
) {
    val weeklyCalendarState = rememberWeeklyCalendarState(selectedDate = state.selectedDate)
    val monthlyCalendarState = rememberMonthCalendarState(selectedDate = state.selectedDate)
    val hazeState = rememberHazeState()
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }
    var showHomeCoachmark by rememberSaveable { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState),
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                containerColor = SdBase,
                contentColor = SdBase,
                bottomBar = {
                    HomeBottomBar(
                        currentRoute = selectedTab,
                        isMonthlyCalendarView = state.isMonthlyCalendarView,
                        onHomeClick = { selectedTab = HomeTab.HOME },
                        onInsightClick = { selectedTab = HomeTab.INSIGHT },
                        onCalendarViewToggle = onToggleCalendarView,
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    Header(
                        modifier = Modifier.padding(vertical = 18.dp),
                        onClickMyPage = onNavigateToMyPage,
                    )
                    Text(
                        text = homeDescriptionText(photoCountByDate),
                        style = AppTypography.p12,
                        color = Gray050,
                    )
                    Text(
                        modifier = Modifier.padding(top = 12.dp, bottom = 36.dp),
                        text = stringResource(string.home_sub_description),
                        style = AppTypography.hd24,
                        color = Gray050,
                    )
                    if (state.isMonthlyCalendarView) {
                        MonthlyCalendar(
                            calendarState = monthlyCalendarState,
                            selectedDate = state.selectedDate,
                            onDateSelected = onDateSelected,
                            photoCountByDate = photoCountByDate,
                        )
                    } else {
                        WeeklyCalendar(
                            calendarState = weeklyCalendarState,
                            selectedDate = state.selectedDate,
                            onDateSelected = onDateSelected,
                            photoCountByDate = photoCountByDate,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        AddPhotoBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            onAddPhoto = onNavigateToImagePicker,
                        )
                    }

                    Button(
                        onClick = { throw RuntimeException("Sentry/Discord 알림 테스트용 크래시") },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Sentry 테스트 (크래시)")
                    }
                }
            }
        }

        if (!state.isMonthlyCalendarView && showHomeCoachmark) {
            HomeCoachmarkOverlay(
                onDismiss = { showHomeCoachmark = false },
                hazeState = hazeState,
            )
        }
    }
}

@Composable
private fun homeDescriptionText(photoCountByDate: Map<LocalDate, Int>): String {
    return when (val total = photoCountByDate.values.sum()) {
        0 -> stringResource(string.home_description_empty)
        in 1..998 -> stringResource(string.home_description_with_count, total.toString())
        else -> stringResource(string.home_description_with_count, "999+")
    }
}

private enum class HomeTab {
    HOME,
    INSIGHT,
}

@Composable
private fun HomeBottomBar(
    currentRoute: HomeTab,
    isMonthlyCalendarView: Boolean,
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit,
    onCalendarViewToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(SdBase)
            .padding(top = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HomeInsightToggle(
            selectedTab = currentRoute,
            onHomeClick = onHomeClick,
            onInsightClick = onInsightClick,
        )
        IconButton(
            modifier = Modifier
                .size(60.dp)
                .gradientBorder(1.dp, ToggleCalendarStrokeGradient, CircleShape),
            onClick = onCalendarViewToggle,
            shape = CircleShape,
            colors = remember {
                IconButtonColors(
                    containerColor = Gray750.copy(alpha = 0.3f),
                    contentColor = Gray050,
                    disabledContainerColor = Gray050,
                    disabledContentColor = Gray050,
                )
            },
        ) {
            Icon(
                painter = painterResource(id = if (isMonthlyCalendarView) drawable.ic_weekly_calendar else drawable.ic_monthly_calendar),
                contentDescription = stringResource(R.string.calendar),
                tint = Gray050,
            )
        }
    }
}

@Composable
private fun HomeInsightToggle(
    selectedTab: HomeTab,
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isHomeSelected = selectedTab == HomeTab.HOME
    val isInsightSelected = selectedTab == HomeTab.INSIGHT

    Row(
        modifier = modifier
.height(60.dp)
.clip(CircleShape)
.background(
    color =  Gray750.copy(alpha = 0.3f),
).gradientBorder(1.dp, ToggleCalendarStrokeGradient, CircleShape)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // 홈
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .width(75.dp)
                    .clip(CircleShape)
                    .background( if (isHomeSelected) PrimBase else Transparent)
                    .selectedTabGradientBorder(selected = isHomeSelected)
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
            // 인사이트
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .width(105.dp)
                    .clip(CircleShape)
                    .background( if (isInsightSelected) PrimBase else Transparent)
                    .selectedTabGradientBorder(selected = isInsightSelected)
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
                    tint =  if (isInsightSelected) White else Gray050,
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

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeScreenState(

        ),
    )
}
