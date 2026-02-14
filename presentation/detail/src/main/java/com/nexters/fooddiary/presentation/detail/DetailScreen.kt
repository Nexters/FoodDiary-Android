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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.R as CoreUiR
import com.nexters.fooddiary.core.ui.component.AddPhotoBox
import com.nexters.fooddiary.core.ui.component.DailyHeader
import com.nexters.fooddiary.core.ui.component.DetailScreenHeader
import com.nexters.fooddiary.core.ui.food.FoodImageCard
import com.nexters.fooddiary.core.ui.food.FoodImageState
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.PretendardFontFamily
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.White
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@Composable
internal fun DetailScreen(
    initialDateString: String = LocalDate.now().toString(),
    viewModel: DetailViewModel = mavericksViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToImagePicker: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val currentOnShowToast by rememberUpdatedState(onShowToast)

    LaunchedEffect(initialDateString) {
        viewModel.syncSelectedDate(initialDateString)
    }

    LaunchedEffect(state.selectedDate) {
        viewModel.loadMealsForDate(state.selectedDate)
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
                DetailEvent.NavigateToImagePicker -> {
                    onNavigateToImagePicker()
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
        onAddPhoto = viewModel::onAddPhoto,
        onEditClick = viewModel::onEditClick,
        onCopyClick = viewModel::onCopyClick,
        onShareClick = viewModel::onShareClick,
    )
}

@Composable
private fun DetailContent(
    selectedDate: LocalDate,
    mealsByDate: Map<LocalDate, DailyMeals>,
    onNavigateBack: () -> Unit = {},
    onPreviousDay: () -> Unit = {},
    onNextDay: () -> Unit = {},
    onAddPhoto: (MealSlot, LocalDate) -> Unit = { _, _ -> },
    onEditClick: (MealSlot, LocalDate) -> Unit = { _, _ -> },
    onCopyClick: (String) -> Unit = {},
    onShareClick: (String, String) -> Unit = { _, _ -> }, // (place, mapLink)
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
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {

            item(key = "detail_header") {
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
                            fontFamily = PretendardFontFamily,
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
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(id = R.string.detail_menu_test),
                                            color = Color.Black
                                        )
                                    },
                                    onClick = { isMoreMenuExpanded = false },
                                )
                            }
                        }
                    }

                }
            }

            item(key = "gap_detail_to_daily") {
                Spacer(modifier = Modifier.height(32.dp))
            }

            stickyHeader(key = selectedDate.toString()) {
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

            item(key = "gap_after_daily_header") {
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
                fontFamily = PretendardFontFamily,
            )

            // FullUI 상태일 때만 수정 버튼 표시
            if (!meal.isEmpty && !meal.isPending) {
                Text(
                    text = stringResource(id = R.string.detail_edit),
                    style = AppTypography.p14.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = White,
                    fontFamily = PretendardFontFamily,
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
                    onAddPhoto = onAddPhoto
                )
            }
        } else {
            // Pending 또는 FullUI 상태: HorizontalPager로 FoodImageCard 표시
            val pagerState = rememberPagerState(pageCount = { meal.imageUrls.size })
            val state = when {
                meal.isPending -> FoodImageState.Pending
                else -> FoodImageState.FullUI(
                    timeText = meal.time,
                    locationText = meal.location,
                    placeText = meal.place,
                    keywords = meal.keywords,
                    onCopyClick = onCopyClick,
                    onShareClick = onShareClick,
                )
            }

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

        // FullUI 상태일 때만 카드 밑에 설명란 표시
        if (!meal.isEmpty && !meal.isPending) {
            MealInfoSection(
                place = meal.place,
                keywords = meal.keywords,
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
                text = place,
                style = AppTypography.hd16,
                fontWeight = FontWeight.Bold,
                color = White,
                fontFamily = PretendardFontFamily,
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
                        fontFamily = PretendardFontFamily,
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
                        fontFamily = PretendardFontFamily,
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
                        fontFamily = PretendardFontFamily,
                    )
                }
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
                mapLink = "",
                imageUrls = emptyList(),
                status = MealCardStatus.EMPTY,
            ),
        ),
    )

    DetailContent(
        selectedDate = today,
        mealsByDate = mockMeals,
    )
}
