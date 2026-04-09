package com.nexters.fooddiary.presentation.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.ActivityNotFoundException
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.R as CoreUiR
import com.nexters.fooddiary.core.ui.component.AddPhotoBox
import com.nexters.fooddiary.core.ui.component.AddPhotoBoxMode
import com.nexters.fooddiary.core.ui.component.DetailScreenHeader
import com.nexters.fooddiary.core.ui.food.FoodImageCard
import com.nexters.fooddiary.core.ui.food.FoodImageState
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.GlassmorphismStyle
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.core.ui.theme.glassmorphism
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private const val DetailHeaderKey = "detail_header"
private const val GapDetailToDailyKey = "gap_detail_to_daily"
private const val DailyHeaderInlineKey = "daily_header_inline"
private const val GapAfterDailyHeaderKey = "gap_after_daily_header"
private val DetailFloatingButtonGlassStyle = GlassmorphismStyle(
    cornerRadius = 999.dp,
    blurRadius = 30.dp,
)

@Composable
internal fun DetailScreen(
    initialDateString: String = LocalDate.now().toString(),
    refreshDiaryDateString: String? = null,
    onRefreshDiaryConsumed: () -> Unit = {},
    viewModel: DetailViewModel = mavericksViewModel(),
    onDeleteSuccess: (LocalDate) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToImagePicker: (LocalDate) -> Unit = {},
    onNavigateToModify: (String) -> Unit = {},
    onShowToast: (String) -> Unit = {},
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val currentOnShowToast by rememberUpdatedState(onShowToast)
    var initialDateToSync by rememberSaveable(initialDateString) {
        mutableStateOf<String?>(initialDateString)
    }

    initialDateToSync?.let { dateString ->
        LaunchedEffect(dateString) {
            viewModel.syncSelectedDate(dateString)
            initialDateToSync = null
        }
    }

    LaunchedEffect(state.selectedDate) {
        viewModel.loadMealsForDate(state.selectedDate)
    }

    LaunchedEffect(refreshDiaryDateString) {
        if (refreshDiaryDateString == null) return@LaunchedEffect
        val syncDate = runCatching { LocalDate.parse(refreshDiaryDateString) }.getOrNull()
        if (syncDate != null && syncDate == state.selectedDate) {
            viewModel.refreshMealsForDate(syncDate)
        }
        onRefreshDiaryConsumed()
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is DetailEvent.CopyMapLink -> {
                    copyToClipboard(currentContext, event.mapLink)
                }
                is DetailEvent.ShareMapLink -> {
                    val placeText = event.place.ifBlank {
                        currentContext.getString(R.string.detail_share_place_fallback)
                    }
                    val prefixText = currentContext.getString(R.string.detail_share_prefix, placeText)
                    val shareMessage = "$prefixText\n${event.mapLink}"
                    shareText(currentContext, shareMessage, currentContext.getString(R.string.detail_share))
                }
                DetailEvent.ShareLinkEmpty -> {
                    currentOnShowToast(currentContext.getString(R.string.detail_share_map_link_empty))
                }
                is DetailEvent.NavigateToImagePicker -> {
                    onNavigateToImagePicker(event.date)
                }
                is DetailEvent.NavigateToModify -> {
                    onNavigateToModify(event.diaryId)
                }
                is DetailEvent.DeleteSuccess -> {
                    onDeleteSuccess(event.date)
                }
                DetailEvent.DeleteEmpty -> {
                    currentOnShowToast(currentContext.getString(R.string.detail_delete_empty))
                }
                DetailEvent.DeleteFailed -> {
                    currentOnShowToast(currentContext.getString(R.string.detail_delete_failed))
                }
            }
        }
    }

    DetailContent(
        selectedDate = state.selectedDate,
        mealsByDate = state.mealsByDate,
        onNavigateBack = onNavigateBack,
        onPreviousDay = viewModel::navigateToPreviousDay,
        onNextDay = viewModel::navigateToNextDay,
        onDeleteClick = viewModel::onDeleteClick,
        onAddPhoto = viewModel::onAddPhoto,
        onEditClick = viewModel::onEditClick,
        onCopyClick = viewModel::onCopyClick,
        onShareClick = viewModel::onShareClick,
        onAddFloatingPhotoClick = { onNavigateToImagePicker(state.selectedDate) },
    )
}

@Composable
private fun DetailContent(
    selectedDate: LocalDate,
    mealsByDate: Map<LocalDate, DailyMeals>,
    onNavigateBack: () -> Unit = {},
    onPreviousDay: () -> Unit = {},
    onNextDay: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onAddPhoto: (MealSlot, LocalDate) -> Unit = { _, _ -> },
    onEditClick: (MealSlot, LocalDate) -> Unit = { _, _ -> },
    onCopyClick: (String) -> Unit = {},
    onShareClick: (String, String) -> Unit = { _, _ -> }, // (place, mapLink)
    onAddFloatingPhotoClick: () -> Unit = {},
) {
    var isMoreMenuExpanded by remember { mutableStateOf(false) }
    val meals = mealsByDate[selectedDate] ?: DailyMeals.empty(selectedDate)
    val mealCards = meals.asOrderedList()
    val hazeState = rememberHazeState()
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    var dailyHeaderHeightPx by remember { mutableIntStateOf(0) }
    var isHeaderVisible by remember { mutableStateOf(true) }
    var previousScrollPosition by remember { mutableIntStateOf(0) }
    val isInlineDailyHeaderVisible by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.any { it.key == DailyHeaderInlineKey }
        }
    }
    val showPinnedDailyHeader by remember {
        derivedStateOf {
            !isInlineDailyHeaderVisible && isHeaderVisible && dailyHeaderHeightPx > 0
        }
    }

    // 스크롤 방향 감지해서 헤더 표시, 미표시
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .map { (index, offset) -> index * 10_000 + offset }
            .distinctUntilChanged()
            .collectLatest { currentPosition ->
                if (currentPosition > previousScrollPosition) {
                    isHeaderVisible = false
                } else if (currentPosition < previousScrollPosition) {
                    isHeaderVisible = true
                }
                previousScrollPosition = currentPosition
            }
    }

    LaunchedEffect(selectedDate) {
        isHeaderVisible = true
    }

    Scaffold(
        containerColor = SdBase,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
                    .background(SdBase),
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {

                item(key = DetailHeaderKey) {
                    DetailScreenHeader(
                        onBackButtonClick = onNavigateBack,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.detail_title),
                                style = AppTypography.hd18,
                                color = White,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            Box(
                                modifier = Modifier.wrapContentSize(align = Alignment.TopEnd)
                            ) {
                                IconButton(onClick = { isMoreMenuExpanded = true }) {
                                    Icon(
                                        painter = painterResource(CoreUiR.drawable.ic_more),
                                        contentDescription = stringResource(id = R.string.detail_more),
                                        tint = Color.White
                                    )
                                }
                                DropdownMenu(
                                    expanded = isMoreMenuExpanded,
                                    onDismissRequest = { isMoreMenuExpanded = false },
                                    offset = DpOffset(x = 0.dp, y = 0.dp),
                                    modifier = Modifier.width(132.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    containerColor = Sd900,
                                    tonalElevation = 0.dp,
                                    shadowElevation = 10.dp,
                                ) {
                                    DropdownMenuItem(
                                        modifier = Modifier.height(46.dp),
                                        text = {
                                            Text(
                                                text = stringResource(id = R.string.detail_menu_delete),
                                                style = AppTypography.p12.copy(letterSpacing = (-0.18).sp),
                                                color = Gray050
                                            )
                                        },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_delete),
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = Gray050,
                                            )
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = Gray050,
                                            trailingIconColor = Gray050,
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp),
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            onDeleteClick()
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                item(key = GapDetailToDailyKey) {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item(key = DailyHeaderInlineKey) {
                    val spacerHeight = with(density) { dailyHeaderHeightPx.toDp() }
                    AnimatedContent(
                        targetState = isHeaderVisible,
                        transitionSpec = {
                            (fadeIn() + slideInVertically(initialOffsetY = { -it / 2 })) togetherWith
                                (fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }))
                        },
                        label = "dailyHeaderToggle",
                    ) { visible ->
                        if (visible) {
                            DailyHeader(
                                date = selectedDate,
                                onPreviousDay = onPreviousDay,
                                onNextDay = onNextDay,
                                hazeState = hazeState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .onSizeChanged { dailyHeaderHeightPx = it.height }
                            )
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(spacerHeight)
                            )
                        }
                    }
                }

                item(key = GapAfterDailyHeaderKey) {
                    Spacer(modifier = Modifier.height(42.dp))
                }

                itemsIndexed(mealCards, key = { _, meal -> meal.id }) { index, meal ->
                    MealSection(
                        meal = meal,
                        mealLabel = meal.slot.toLabel(),
                        onAddPhoto = { onAddPhoto(meal.slot, meal.date) },
                        onEditClick = { onEditClick(meal.slot, meal.date) },
                        onCopyClick = { onCopyClick(meal.mapLink) },
                        onShareClick = { onShareClick(meal.place, meal.mapLink) },
                    )

                    if (index != mealCards.lastIndex) {
                        Spacer(modifier = Modifier.height(42.dp))
                    }
                }
            }

            AnimatedVisibility(
                visible = showPinnedDailyHeader,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
                label = "pinnedDailyHeaderToggle",
            ) {
                DailyHeader(
                    date = selectedDate,
                    onPreviousDay = onPreviousDay,
                    onNextDay = onNextDay,
                    hazeState = hazeState,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 24.dp)
                    .size(60.dp)
                    .glassmorphism(
                        hazeState = hazeState,
                        style = DetailFloatingButtonGlassStyle,
                    ),
                onClick = onAddFloatingPhotoClick,
                shape = CircleShape,
                colors = IconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Gray050,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Gray050,
                ),
            ) {
                Icon(
                    painter = painterResource(id = CoreUiR.drawable.ic_add),
                    contentDescription = null,
                    tint = Gray050,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealSection(
    meal: MealCardUiModel,
    mealLabel: String,
    onAddPhoto: () -> Unit,
    onEditClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // 식사 라벨 (아침)
            Text(
                text = mealLabel,
                style = AppTypography.p18,
                color = White,
            )

            // Ready 상태일 때만 수정 버튼 표시
            if (!meal.isEmpty && !meal.isPending) {
                Text(
                    text = stringResource(id = R.string.detail_edit),
                    style = AppTypography.p14.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = White,
                    modifier = Modifier.clickable { onEditClick() }
                )
            }
        }


        // 식사 이미지 카드 또는 AddPhotoBox
        if (meal.isEmpty) {
            // Empty 상태: AddPhotoBox 사용
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                AddPhotoBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    mode = AddPhotoBoxMode.NO_IMAGE_RECORDED_DETAIL,
                    onAddPhoto = onAddPhoto
                )
            }
        } else {
            if (meal.isPending) {
                val firstImageUrl = meal.imageUrls.firstOrNull()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    FoodImageCard(
                        imageUrl = firstImageUrl.orEmpty(),
                        state = if (firstImageUrl.isNullOrEmpty()) {
                            FoodImageState.Pending
                        } else {
                            FoodImageState.Processing
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            } else {
                // Ready 상태: HorizontalPager로 FoodImageCard 표시
                val pagerState = rememberPagerState(pageCount = { meal.imageUrls.size })
                val state = FoodImageState.Ready(
                    timeText = meal.time,
                    locationText = meal.location,
                )

                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    FoodImageCard(
                        imageUrl = meal.imageUrls[page],
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            }
        }

        // Ready 상태일 때만 카드 밑에 설명란 표시
        if (!meal.isEmpty && !meal.isPending) {
            MealInfoSection(
                place = meal.place,
                keywords = meal.keywords,
                note = meal.note,
                onCopyClick = onCopyClick,
                onShareClick = onShareClick,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealInfoSection(
    place: String,
    keywords: List<String>,
    note: String,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
    ) {
        // 장소명 + 복사/공유 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 장소명
            Text(
                text = place.ifBlank { stringResource(id = R.string.detail_place_empty_guide) },
                style = AppTypography.p12,
                fontWeight = FontWeight.Bold,
                color = White,
            )

            // 복사/공유 버튼
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp), // 두 버튼 사이의 간격
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 복사 버튼
                Row(
                    modifier = Modifier.clickable { onCopyClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // 아이콘과 텍스트 사이 간격
                ) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_copy),
                        contentDescription = stringResource(id = R.string.detail_copy),
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.detail_copy),
                        color = White,
                        style = AppTypography.p12,
                        fontWeight = FontWeight.Medium,
                    )
                }

                // 공유 버튼
                Row(
                    modifier = Modifier.clickable { onShareClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_share),
                        contentDescription = stringResource(id = R.string.detail_share),
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.detail_share),
                        color = White,
                        style = AppTypography.p12,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        // 키워드
        if (keywords.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                keywords.forEach { keyword ->
                    Text(
                        text = keyword,
                        color = White,
                        style = AppTypography.p12,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        if (note.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF1D1C27),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_ai_analysis),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = stringResource(id = R.string.detail_ai_summary_title),
                        color = White,
                        style = AppTypography.p12,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Text(
                    text = note,
                    color = White,
                    style = AppTypography.p12,
                )
            }
        }
    }
}

@Composable
private fun MealSlot.toLabel(): String {
    return when (this) {
        MealSlot.BREAKFAST -> stringResource(id = R.string.detail_meal_breakfast)
        MealSlot.LUNCH -> stringResource(id = R.string.detail_meal_lunch)
        MealSlot.DINNER -> stringResource(id = R.string.detail_meal_dinner)
        MealSlot.SNACK -> stringResource(id = R.string.detail_meal_snack)
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("map_link", text))
}

private fun shareText(context: Context, text: String, chooserTitle: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooserIntent = Intent.createChooser(sendIntent, chooserTitle)
    try {
        context.startActivity(chooserIntent)
    } catch (_: ActivityNotFoundException) {
        // 공유 가능한 앱이 없는 환경에서는 크래시 없이 무시
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun DetailScreenPreview() {
    val today = LocalDate.now()
    val mockMeals = mapOf(
        today to DailyMeals(
            breakfast = MealCardUiModel(
                id = "1",
                date = today,
                slot = MealSlot.BREAKFAST,
                time = "07:00",
                location = "마포구",
                place = "호진이네",
                keywords = listOf("#양장피", "#어향동고"),
                note = "테이블 가득 차려진 한 상 차림의 음식 사진입니다.",
                mapLink = "https://map.naver.com/p/entry/place/123456",
                imageUrls = listOf("https://picsum.photos/300"),
                status = MealCardStatus.READY,
            ),
            lunch = MealCardUiModel(
                id = "2",
                date = today,
                slot = MealSlot.LUNCH,
                time = "12:30",
                location = "강남구",
                place = "",
                keywords = emptyList(),
                note = "",
                mapLink = "",
                imageUrls = listOf("https://picsum.photos/300"),
                status = MealCardStatus.PENDING,
            ),
            dinner = MealCardUiModel(
                id = "3",
                date = today,
                slot = MealSlot.DINNER,
                time = "19:00",
                location = "",
                place = "",
                keywords = emptyList(),
                note = "",
                mapLink = "",
                imageUrls = emptyList(),
                status = MealCardStatus.EMPTY,
            ),
            snack = MealCardUiModel(
                id = "4",
                date = today,
                slot = MealSlot.SNACK,
                time = "16:00",
                location = "서초구",
                place = "카페",
                keywords = listOf("#커피"),
                note = "간단히 커피와 디저트를 먹었어요.",
                mapLink = "",
                imageUrls = listOf("https://picsum.photos/301"),
                status = MealCardStatus.READY,
            ),
        ),
    )

    DetailContent(
        selectedDate = today,
        mealsByDate = mockMeals,
    )
}
