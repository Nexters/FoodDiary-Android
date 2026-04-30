package com.nexters.fooddiary.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.mvrx.compose.collectAsState as collectMavericksState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
internal fun HomeEntryScreen(
    onNavigateToImagePicker: (LocalDate) -> Unit = {},
    onNavigateToDetail: (LocalDate) -> Unit = {},
    onNavigateToPermissionGuide: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    isMonthlyCalendarView: Boolean = false,
    refreshDiaryDateString: String? = null,
    onRefreshDiaryConsumed: () -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
    viewModel: HomeViewModel = mavericksViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectMavericksState()
    val photoCountByDate by viewModel.photoCountByDate.collectAsState()
    val photoUrlsByDate by viewModel.photoUrlsByDate.collectAsState()
    val currentOnNavigateToDetail by rememberUpdatedState(onNavigateToDetail)
    val lifecycleOwner = LocalLifecycleOwner.current
    var mediaAccessState by remember {
        mutableStateOf(PermissionUtil.getMediaAccessState(context))
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
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                mediaAccessState = PermissionUtil.getMediaAccessState(context)
                viewModel.refreshAddableImageState()
                if (mediaAccessState == PermissionUtil.MediaAccessState.FULL) {
                    viewModel.loadInitialData()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(refreshDiaryDateString) {
        if (refreshDiaryDateString == null) return@LaunchedEffect
        val syncDate = runCatching { LocalDate.parse(refreshDiaryDateString) }.getOrNull()
        if (syncDate != null) {
            viewModel.onDiaryUpdated(syncDate)
        }
        onRefreshDiaryConsumed()
    }

    if (mediaAccessState == PermissionUtil.MediaAccessState.FULL) {
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
                selectedDateImageStatesByUrl = state.selectedDateImageStatesByUrl,
            ),
            onShowSnackBar = onShowSnackBar,
            onMonthChanged = viewModel::loadPhotosForMonth,
            photoCountByDate = photoCountByDate,
            photoUrlsByDate = photoUrlsByDate,
        )
    } else {
        HomePermissionBlockedScreen(
            onOpenSettings = onNavigateToPermissionGuide,
        )
    }
}
