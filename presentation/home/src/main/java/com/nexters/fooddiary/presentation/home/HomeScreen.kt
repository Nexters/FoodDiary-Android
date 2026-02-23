package com.nexters.fooddiary.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.YearMonth
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.core.ui.calendar.MonthlyCalendar
import com.nexters.fooddiary.core.ui.calendar.WeeklyCalendar
import com.nexters.fooddiary.core.ui.calendar.rememberMonthCalendarState
import com.nexters.fooddiary.core.ui.calendar.rememberWeeklyCalendarState
import com.nexters.fooddiary.core.ui.component.AddPhotoBox
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.GlassmorphismStyle
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.glassmorphism
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import com.nexters.fooddiary.core.ui.calendar.WeeklyCalendar
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

private val BottomBarGlassStyle = GlassmorphismStyle(
    cornerRadius = 999.dp,
    blurRadius = 30.dp,
)

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToImagePicker: () -> Unit = {},
    onNavigateToDetail: (LocalDate) -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
    viewModel: HomeViewModel = mavericksViewModel(),
) {
    val state by viewModel.collectAsState()
    val photoCountByDate by viewModel.photoCountByDate.collectAsState(initial = emptyMap())
    val currentOnNavigateToDetail by rememberUpdatedState(onNavigateToDetail)

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeEvent.NavigateToDetail -> currentOnNavigateToDetail(event.date)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    HomeScreen(
        state = state,
        onDateSelected = viewModel::onDateSelected,
        onMonthChanged = viewModel::loadPhotosForMonth,
        onToggleCalendarView = viewModel::onToggleCalendarView,
        onNavigateToImagePicker = onNavigateToImagePicker,
        onNavigateToMyPage = onNavigateToMyPage,
        onShowSnackBar = onShowSnackBar,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(
    state: HomeScreenState,
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit = {},
    onMonthChanged: (YearMonth) -> Unit = {},
    onToggleCalendarView: () -> Unit = {},
    onNavigateToImagePicker: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
) {
    val screenHazeState = rememberHazeState()
    val scrollState = rememberScrollState()
    val weeklyCalendarState = rememberWeeklyCalendarState(selectedDate = state.selectedDate)
    val monthlyCalendarState = rememberMonthCalendarState(selectedDate = state.selectedDate)
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SdBase,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            HomeBottomBar(
                currentRoute = selectedTab,
                isMonthlyCalendarView = state.isMonthlyCalendarView,
                onHomeClick = { selectedTab = HomeTab.HOME },
                onInsightClick = { selectedTab = HomeTab.INSIGHT },
                onCalendarViewToggle = onToggleCalendarView,
                hazeState = screenHazeState,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
            )
        },
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(screenHazeState)
                .background(SdBase)
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                    bottom = 0.dp,
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
            ) {
                Header(
                    modifier = Modifier.padding(vertical = 18.dp),
                    onClickMyPage = onNavigateToMyPage,
                )
                WeekCountDescription(diaryCountByWeek = state.diaryCountByWeek)
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
                        photoCountByDate = state.diaryCountByDate,
                    )
                } else {
                    WeeklyCalendar(
                        calendarState = weeklyCalendarState,
                        selectedDate = state.selectedDate,
                        onDateSelected = onDateSelected,
                        photoCountByDate = state.diaryCountByDate,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    AddPhotoBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        onAddPhoto = onNavigateToImagePicker,
                    )
                }
                Button(
                    onClick = { throw RuntimeException("Sentry/Discord 알림 테스트용 크래시") },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Sentry 테스트 (크래시)")
                }
                Button(
                    onClick = {
                        onShowSnackBar(
                            SnackBarData(
                                message = "리퀴드 글래스 스낵바 테스트",
                                iconRes = drawable.ic_check_circle,
                            )
                        )
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("스낵바 테스트")
                }
                Spacer(modifier = Modifier.height(144.dp))
            }
        }
    }
}

@Composable
private fun WeekCountDescription(
    diaryCountByWeek: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = homeDescriptionText(diaryCountByWeek),
        style = AppTypography.p12,
        color = Gray050,
    )
}

@Composable
private fun CalendarSection(
    selectedDate: LocalDate,
    isMonthlyCalendarView: Boolean,
    diaryCountByDate: Map<LocalDate, Int>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    onNavigateToImagePicker: () -> Unit,
) {
    val weeklyCalendarState = rememberWeeklyCalendarState(selectedDate = selectedDate)
    val monthlyCalendarState = rememberMonthCalendarState(selectedDate = selectedDate)

    if (isMonthlyCalendarView) {
        MonthlyCalendar(
            calendarState = monthlyCalendarState,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            onMonthChanged = onMonthChanged,
            photoCountByDate = diaryCountByDate,
        )
    } else {
        WeeklyCalendar(
            calendarState = weeklyCalendarState,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            photoCountByDate = diaryCountByDate,
            today = LocalDate.now(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        AddPhotoBox(
            modifier = Modifier.fillMaxWidth(),
            onAddPhoto = onNavigateToImagePicker,
        )
    }
}

@Composable
private fun HomeBottomBar(
    isMonthlyCalendarView: Boolean,
    currentRoute: HomeTab,
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit,
    onCalendarViewToggle: () -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier,
) {
    var displayMonthly by remember(isMonthlyCalendarView) { mutableStateOf(isMonthlyCalendarView) }
    LaunchedEffect(isMonthlyCalendarView) {
        displayMonthly = isMonthlyCalendarView
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HomeInsightToggle(
            selectedTab = currentRoute,
            onHomeClick = onHomeClick,
            onInsightClick = onInsightClick,
            hazeState = hazeState,
        )
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
                painter = painterResource(id = if (displayMonthly) drawable.ic_weekly_calendar else drawable.ic_monthly_calendar),
                contentDescription = stringResource(string.calendar),
                tint = Gray050,
            )
        }
    }
}

@Composable
private fun homeDescriptionText(photoCountByWeek: Int): String {
    return when (photoCountByWeek) {
        0 -> stringResource(string.home_description_empty)
        in 1..998 -> stringResource(string.home_description_with_count, photoCountByWeek.toString())
        else -> stringResource(string.home_description_with_count, "999+")
    }
}

private enum class HomeTab {
    HOME,
    INSIGHT,
}

@Composable
private fun HomeInsightToggle(
    selectedTab: HomeTab,
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier,
) {
    val isHomeSelected = selectedTab == HomeTab.HOME
    val isInsightSelected = selectedTab == HomeTab.INSIGHT

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
        // 홈
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
        // 인사이트
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

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeScreenState(
        ),
    )
}
