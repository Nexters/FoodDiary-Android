package com.nexters.fooddiary.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.nexters.fooddiary.core.ui.food.FoodImageStackView
import com.nexters.fooddiary.core.ui.food.FoodImageState
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToImagePicker: () -> Unit = {},
    onNavigateToDetail: (LocalDate) -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    isMonthlyCalendarView: Boolean = false,
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

    HomeScreen(
        state = state,
        isMonthlyCalendarView = isMonthlyCalendarView,
        photoCountByDate = photoCountByDate,
        onDateSelected = viewModel::onDateSelected,
        onCardStackClick = viewModel::onCardStackClicked,
        onNavigateToImagePicker = onNavigateToImagePicker,
        onNavigateToMyPage = onNavigateToMyPage,
        selectedDateImageUrls = selectedDateImageUrls(
            weeklyPhotosByDate = state.weeklyPhotosByDate,
            selectedDate = state.selectedDate,
        ),
        onShowSnackBar = onShowSnackBar,
        modifier = modifier,
    )
}

internal fun selectedDateImageUrls(
    weeklyPhotosByDate: Map<LocalDate, List<String>>,
    selectedDate: LocalDate,
): List<String> = weeklyPhotosByDate[selectedDate].orEmpty()

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeScreenState = HomeScreenState(),
    isMonthlyCalendarView: Boolean = false,
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
    onDateSelected: (LocalDate) -> Unit = {},
    onCardStackClick: () -> Unit = {},
    onNavigateToImagePicker: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    selectedDateImageUrls: List<String> = emptyList(),
    onShowSnackBar: (SnackBarData) -> Unit = {},
) {
    val screenHazeState = rememberHazeState()
    val scrollState = rememberScrollState()
    val weeklyCalendarState = rememberWeeklyCalendarState(selectedDate = state.selectedDate)
    val monthlyCalendarState = rememberMonthCalendarState(selectedDate = state.selectedDate)

    Box(
        modifier = modifier
            .fillMaxSize()
            .hazeSource(screenHazeState)
            .background(SdBase)
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
                if (isMonthlyCalendarView) {
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
                    Spacer(modifier = Modifier.height(43.dp))
                    if (selectedDateImageUrls.isNotEmpty()) {
                        FoodImageStackView(
                            imageUrls = selectedDateImageUrls,
                            state = FoodImageState.Ready(
                                timeText = "시간",
                                locationText = "위치",
                            ),
                            onCardClick = onCardStackClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .aspectRatio(1f),
                        )
                    } else {
                        AddPhotoBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            onAddPhoto = onNavigateToImagePicker,
                        )
                    }
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

@Composable
private fun homeDescriptionText(photoCountByDate: Map<LocalDate, Int>): String {
    return when (val total = photoCountByDate.values.sum()) {
        0 -> stringResource(string.home_description_empty)
        in 1..998 -> stringResource(string.home_description_with_count, total.toString())
        else -> stringResource(string.home_description_with_count, "999+")
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
