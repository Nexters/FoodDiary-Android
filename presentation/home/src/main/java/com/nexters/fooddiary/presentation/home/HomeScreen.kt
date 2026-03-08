package com.nexters.fooddiary.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.mvrx.compose.collectAsState as collectMavericksState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.core.ui.calendar.MonthlyCalendar
import com.nexters.fooddiary.core.ui.calendar.WeeklyCalendar
import com.nexters.fooddiary.core.ui.calendar.rememberMonthCalendarState
import com.nexters.fooddiary.core.ui.calendar.rememberWeeklyCalendarState
import com.nexters.fooddiary.core.ui.component.AddPhotoBox
import com.nexters.fooddiary.core.ui.component.AddPhotoBoxMode
import com.nexters.fooddiary.core.ui.component.Header
import com.nexters.fooddiary.core.ui.food.FoodImageCard
import com.nexters.fooddiary.core.ui.food.FoodImageState
import com.nexters.fooddiary.core.ui.food.FoodImageStackView
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.R as coreR
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.YearMonth

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToImagePicker: (LocalDate) -> Unit = {},
    onNavigateToDetail: (LocalDate) -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    isMonthlyCalendarView: Boolean = false,
    pushSyncDateString: String? = null,
    uploadPendingDateString: String? = null,
    onPushSyncConsumed: () -> Unit = {},
    onUploadPendingConsumed: () -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
    viewModel: HomeViewModel = mavericksViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectMavericksState()
    val photoCountByDate by viewModel.photoCountByDate.collectAsState()
    val photoUrlsByDate by viewModel.photoUrlsByDate.collectAsState()
    val currentOnNavigateToDetail by rememberUpdatedState(onNavigateToDetail)
    val lifecycleOwner = LocalLifecycleOwner.current

    val requiredPermission = PermissionUtil.getRequiredMediaPermission()
    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadInitialData()
            viewModel.refreshAddableImageState()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeEvent.NavigateToDetail -> currentOnNavigateToDetail(event.date)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
        if (!PermissionUtil.hasMediaPermission(context)) {
            mediaPermissionLauncher.launch(requiredPermission)
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshAddableImageState()
                if (!PermissionUtil.hasMediaPermission(context)) {
                    mediaPermissionLauncher.launch(requiredPermission)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(pushSyncDateString) {
        if (pushSyncDateString == null) return@LaunchedEffect
        val syncDate = runCatching { LocalDate.parse(pushSyncDateString) }.getOrNull()
        if (syncDate != null) {
            viewModel.onDiaryUpdated(syncDate)
        }
        onPushSyncConsumed()
    }

    LaunchedEffect(uploadPendingDateString) {
        val dateString = uploadPendingDateString ?: return@LaunchedEffect
        runCatching { LocalDate.parse(dateString) }
            .getOrNull()
            ?.let(viewModel::onDiaryUploadPending)
        onUploadPendingConsumed()
    }

    HomeScreen(
        state = state,
        isMonthlyCalendarView = isMonthlyCalendarView,
        onDateSelected = viewModel::onDateSelected,
        onCardStackClick = viewModel::onCardStackClicked,
        onNavigateToImagePicker = onNavigateToImagePicker,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToMyPage = onNavigateToMyPage,
        selectedDateImageUrls = selectedDateImageUrls(
            weeklyPhotosByDate = state.weeklyPhotosByDate,
            selectedDate = state.selectedDate,
        ),
        isSelectedDatePending = state.pendingDates.contains(state.selectedDate),
        onShowSnackBar = onShowSnackBar,
        onMonthChanged = viewModel::loadPhotosForMonth,
        photoCountByDate = photoCountByDate,
        photoUrlsByDate = photoUrlsByDate,
        modifier = modifier,
    )
}

internal fun selectedDateImageUrls(
    weeklyPhotosByDate: Map<LocalDate, List<String>>,
    selectedDate: LocalDate,
): List<String> = weeklyPhotosByDate[selectedDate].orEmpty()

@Composable
private fun HomeScreen(
    state: HomeScreenState,
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit = {},
    isMonthlyCalendarView: Boolean = false,
    onCardStackClick: () -> Unit = {},
    onNavigateToImagePicker: (LocalDate) -> Unit = {},
    onNavigateToDetail: (LocalDate) -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    selectedDateImageUrls: List<String> = emptyList(),
    isSelectedDatePending: Boolean = false,
    onShowSnackBar: (SnackBarData) -> Unit = {},
    onMonthChanged: (YearMonth) -> Unit = {},
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
    photoUrlsByDate: Map<LocalDate, List<String>> = emptyMap(),
) {
    val screenHazeState = rememberHazeState()
    val scrollState = rememberScrollState()
    val weeklyCalendarState = rememberWeeklyCalendarState(selectedDate = state.selectedDate)
    val monthlyCalendarState = rememberMonthCalendarState(selectedDate = state.selectedDate)
    val canShowAddPhoto = state.hasAddableImagesForSelectedDate

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
                    leftIconResId = coreR.drawable.ic_app_icon,
                    leftIconColorFilter = null,
                    onClickMyPage = onNavigateToMyPage,
                )
                WeekCountDescription(diaryCountByWeek = state.diaryCountByWeek)
                Text(
                    modifier = Modifier.padding(top = 12.dp, bottom = 36.dp),
                    text = stringResource(string.home_sub_description, state.userName),
                    style = AppTypography.hd24,
                    color = Gray050,
                )
                if (isMonthlyCalendarView) {
                    MonthlyCalendar(
                        calendarState = monthlyCalendarState,
                        selectedDate = state.selectedDate,
                        onDateSelected = { date ->
                            onDateSelected(date)
                            if (!date.isAfter(LocalDate.now())) {
                                onNavigateToDetail(date)
                            }
                        },
                        onMonthChanged = onMonthChanged,
                        photoCountByDate = photoCountByDate,
                        photoUrlsByDate = photoUrlsByDate,
                    )
                } else {
                    WeeklyCalendar(
                        calendarState = weeklyCalendarState,
                        selectedDate = state.selectedDate,
                        onDateSelected = onDateSelected,
                        photoCountByDate = state.diaryCountByDate,
                    )
                    Spacer(modifier = Modifier.height(43.dp))
                    if (selectedDateImageUrls.isNotEmpty()) {
                        FoodImageStackView(
                            imageUrls = selectedDateImageUrls,
                            state = state.selectedDateImageState,
                            stateByImageUrl = state.selectedDateImageStatesByUrl,
                            onCardClick = onCardStackClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .aspectRatio(1f),
                        )
                    } else if (isSelectedDatePending) {
                        FoodImageCard(
                            imageUrl = "",
                            state = FoodImageState.Pending,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .aspectRatio(1f),
                        )
                    } else if (canShowAddPhoto == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .aspectRatio(1f),
                        )
                    } else {
                        AddPhotoBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            mode = if (canShowAddPhoto) {
                                AddPhotoBoxMode.ADDABLE
                            } else {
                                AddPhotoBoxMode.NO_IMAGE_RECORDED
                            },
                            onAddPhoto = { onNavigateToImagePicker(state.selectedDate) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(144.dp))
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
private fun homeDescriptionText(photoCountByWeek: Int): String {
    return when (photoCountByWeek) {
        0 -> stringResource(string.home_description_empty)
        in 1..998 -> stringResource(string.home_description_with_count, photoCountByWeek.toString())
        else -> stringResource(string.home_description_with_count, "999+")
    }
}


@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeScreenState(
            userName = "소연"
        ),
    )
}
