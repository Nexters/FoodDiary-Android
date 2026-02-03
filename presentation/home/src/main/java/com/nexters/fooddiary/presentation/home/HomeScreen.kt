package com.nexters.fooddiary.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.nexters.fooddiary.core.ui.component.AddPhotoBox
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray750
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.presentation.calendar.MonthlyCalendar
import com.nexters.fooddiary.presentation.calendar.WeeklyCalendar
import com.nexters.fooddiary.presentation.calendar.rememberMonthCalendarState
import com.nexters.fooddiary.presentation.calendar.rememberWeeklyCalendarState
import java.time.LocalDate

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToImagePicker: () -> Unit = {},
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
) {
    val weeklyCalendarState = rememberWeeklyCalendarState(selectedDate = state.selectedDate)
    val monthlyCalendarState = rememberMonthCalendarState(selectedDate = state.selectedDate)
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
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
                    onClickMyPage = { },
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
            }
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
            modifier = modifier
        )
        IconButton(
            modifier = modifier.size(60.dp),
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
    Row(
        modifier = modifier
            .height(60.dp)
            .clip(CircleShape)
            .background(Gray750.copy(alpha = 0.3f))
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
                .background(if (selectedTab == HomeTab.HOME) PrimBase else Transparent)
                .clickable(onClick = onHomeClick)
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(drawable.ic_home),
                contentDescription = stringResource(string.home_nav_home),
                tint = if (selectedTab == HomeTab.HOME) White else Gray050,
                modifier = Modifier.size(20.dp),
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(string.home_nav_home),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (selectedTab == HomeTab.HOME) White else Gray050,
            )
        }
        // 인사이트
        Row(
            modifier = Modifier
                .height(44.dp)
                .width(105.dp)
                .clip(CircleShape)
                .background(if (selectedTab == HomeTab.INSIGHT) PrimBase else Transparent)
                .clickable(onClick = onInsightClick)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(drawable.ic_insights),
                contentDescription = stringResource(string.home_nav_insight),
                tint = if (selectedTab == HomeTab.INSIGHT) White else Gray050,
                modifier = Modifier.size(20.dp),
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(string.home_nav_insight),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (selectedTab == HomeTab.INSIGHT) White else Gray050,
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeScreenState(),
    )
}
