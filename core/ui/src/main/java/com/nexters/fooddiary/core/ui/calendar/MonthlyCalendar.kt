package com.nexters.fooddiary.core.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.calendar.theme.CalendarColors
import com.nexters.fooddiary.core.ui.calendar.theme.calendarColors
import com.nexters.fooddiary.core.ui.theme.AppTypography.hd16
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.Gray700
import com.nexters.fooddiary.core.ui.theme.Gray900
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import android.view.Choreographer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.coroutines.resume

private val MonthDayCellCornerShape = RoundedCornerShape(4.dp)

private fun onDayClicked(
    day: CalendarDay,
    coroutineScope: CoroutineScope,
    calendarState: CalendarState,
    onDateSelected: (LocalDate) -> Unit,
) {
    if (day.position != DayPosition.MonthDate) {
        coroutineScope.launch {
            calendarState.animateScrollToMonth(YearMonth.from(day.date))
            onDateSelected(day.date)
        }
    } else {
        onDateSelected(day.date)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCalendar(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    locale: Locale = Locale.getDefault(),
    colors: CalendarColors = calendarColors(),
    onMonthChanged: (YearMonth) -> Unit = {},
    photoCountByDate: Map<LocalDate, Int> = emptyMap(),
    photoUrlsByDate: Map<LocalDate, List<String>> = emptyMap(),
) {
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = remember(calendarState) {
        derivedStateOf { calendarState.firstVisibleMonth.yearMonth }
    }
    var monthBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.firstVisibleMonth.yearMonth }
            .distinctUntilChanged()
            .collect { yearMonth ->
                onMonthChanged(yearMonth)
            }
    }

    LaunchedEffect(monthBottomSheetVisible) {
        if (monthBottomSheetVisible) {
            bottomSheetState.show()
        }
    }

    Column(modifier = modifier) {
        MonthCalendarHeader(
            yearMonth = visibleMonth.value,
            colors = colors,
            onClick = { monthBottomSheetVisible = true },
        )
        if (monthBottomSheetVisible) {
            MonthSelectBottomSheet(
                sheetState = bottomSheetState,
                onDismissRequest = { monthBottomSheetVisible = false },
                currentYearMonth = visibleMonth.value,
                locale = locale,
                colors = colors,
                onMonthSelected = { targetYearMonth ->
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        monthBottomSheetVisible = false
                        calendarState.animateScrollToMonth(targetYearMonth)
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .shadow(4.dp, CalendarContainerShape, spotColor = Gray600.copy(alpha = 0.25f))
                .clip(CalendarContainerShape)
                .background(Sd900)
                .border(1.dp, Gray600.copy(alpha = 0.6f), CalendarContainerShape)
                .padding(16.dp),
        ) {
            MonthWeekDaysHeader(
                locale = locale,
                firstDayOfWeek = calendarState.firstDayOfWeek,
                colors = colors
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalCalendar(
                state = calendarState,
                dayContent = { day ->
                    key(day.date) {
                        MonthDayCell(
                            day = day,
                            isSelected = day.date == selectedDate,
                            photoCount = photoCountByDate[day.date] ?: 0,
                            photoUrls = photoUrlsByDate[day.date].orEmpty().take(2),
                            colors = colors,
                            onClick = { onDayClicked(day, coroutineScope, calendarState, onDateSelected) },
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun MonthCalendarHeader(
    yearMonth: YearMonth,
    colors: CalendarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.headerText,
        )
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = "월 선택",
            modifier = Modifier.size(24.dp),
            tint = colors.iconTint,
        )
    }
}

private const val ITEM_HEIGHT_DP = 35
private const val VISIBLE_ITEMS = 5

private fun LazyListLayoutInfo.centeredItemIndex(fallback: Int): Int {
    val centerY = viewportCenterY
    return visibleItemsInfo.firstOrNull { it.covers(centerY) }?.index
        ?: visibleItemsInfo.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - centerY) }?.index
        ?: fallback
}

private val LazyListLayoutInfo.viewportCenterY: Int
    get() = (viewportStartOffset + viewportEndOffset) / 2

private fun LazyListItemInfo.covers(y: Int): Boolean = y in offset until offset + size

private fun LazyListLayoutInfo.isItemCentered(itemIndex: Int): Boolean =
    visibleItemsInfo.any { it.index == itemIndex && it.covers(viewportCenterY) }

private suspend fun awaitNextFrame() {
    suspendCancellableCoroutine { cont ->
        Choreographer.getInstance().postFrameCallback {
            cont.resume(Unit)
        }
    }
}

private fun scrollOffsetToCenterFromLayoutInfo(info: LazyListLayoutInfo): Int {
    val viewportHeight = info.viewportSize.height
    val itemHeight = info.visibleItemsInfo.firstOrNull()?.size
        ?: return viewportHeight * (VISIBLE_ITEMS - 1) / (2 * VISIBLE_ITEMS) // estimate: itemHeight ≈ viewportHeight/VISIBLE_ITEMS
    return (viewportHeight / 2 - itemHeight / 2).coerceIn(0, maxOf(0, viewportHeight - itemHeight))
}

private suspend fun snapPickerToCenterWhenScrollEnds(
    listState: LazyListState,
    itemCount: Int,
) {
    snapshotFlow { listState.isScrollInProgress }
        .distinctUntilChanged()
        .filter { !it }
        .collect {
            awaitNextFrame()
            val info = listState.layoutInfo
            if (info.visibleItemsInfo.isEmpty()) return@collect
            val fallbackIndex = listState.firstVisibleItemIndex.coerceIn(0, (itemCount - 1).coerceAtLeast(0))
            val indexToCenter = info.centeredItemIndex(fallbackIndex).coerceIn(0, itemCount - 1)
            if (info.isItemCentered(indexToCenter)) return@collect
            val scrollOffsetToCenter = scrollOffsetToCenterFromLayoutInfo(info)
            listState.animateScrollToItem(index = indexToCenter, scrollOffset = scrollOffsetToCenter)
        }
}

private fun centeredYearMonth(
    years: List<Int>,
    startYear: Int,
    yearState: LazyListState,
    monthState: LazyListState,
    fallbackYear: Int,
    fallbackMonth: Int
): YearMonth {
    val y = years.getOrNull(yearState.layoutInfo.centeredItemIndex((fallbackYear - startYear).coerceIn(0, years.lastIndex))) ?: fallbackYear
    val m = (monthState.layoutInfo.centeredItemIndex((fallbackMonth - 1).coerceIn(0, 11)) + 1).coerceIn(1, 12)
    return YearMonth.of(y, m)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthSelectBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    currentYearMonth: YearMonth,
    locale: Locale,
    colors: CalendarColors,
    onMonthSelected: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val startYear = YearMonth.now().year - CALENDAR_YEAR_RANGE
    val years = remember(startYear) { (startYear..startYear + CALENDAR_YEAR_RANGE * 2).toList() }

    var selectedYear by remember(currentYearMonth) { mutableStateOf(currentYearMonth.year) }
    var selectedMonth by remember(currentYearMonth) { mutableStateOf(currentYearMonth.monthValue) }

    val yearListState = rememberLazyListState()
    val monthListState = rememberLazyListState()

    LaunchedEffect(currentYearMonth) {
        yearListState.animateScrollToItem((currentYearMonth.year - startYear).coerceIn(0, years.lastIndex))
        monthListState.animateScrollToItem(currentYearMonth.monthValue - 1)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Sd900,
        dragHandle = null,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
        ) {
            CloseButton(modifier = Modifier.align(Alignment.End), onClick = onDismissRequest)

            MonthPickerContent(
                years = years,
                startYear = startYear,
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                onYearClick = { selectedYear = it },
                onMonthClick = { selectedMonth = it },
                yearListState = yearListState,
                monthListState = monthListState,
                onConfirm = { onMonthSelected(centeredYearMonth(years, startYear, yearListState, monthListState, selectedYear, selectedMonth)) },
            )
        }
    }
}

@Composable
private fun MonthPickerContent(
    years: List<Int>,
    startYear: Int,
    selectedYear: Int,
    selectedMonth: Int,
    onYearClick: (Int) -> Unit,
    onMonthClick: (Int) -> Unit,
    yearListState: LazyListState,
    monthListState: LazyListState,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pickerHeight = (ITEM_HEIGHT_DP * VISIBLE_ITEMS).dp
    val contentPadding = PaddingValues(vertical = (ITEM_HEIGHT_DP * (VISIBLE_ITEMS - 1) / 2).dp)

    val centeredYearIndex by remember {
        derivedStateOf {
            yearListState.layoutInfo.centeredItemIndex((selectedYear - startYear).coerceIn(0, years.lastIndex))
        }
    }
    val centeredMonthIndex by remember {
        derivedStateOf {
            monthListState.layoutInfo.centeredItemIndex((selectedMonth - 1).coerceIn(0, 11))
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        SelectionBand()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PickerColumn(
                itemCount = years.size,
                pickerHeight = pickerHeight,
                contentPadding = contentPadding,
                listState = yearListState,
                centeredIndex = centeredYearIndex,
                label = { "${years[it]}년" },
                onClick = { onYearClick(years[it]) },
            )
            PickerColumn(
                itemCount = 12,
                pickerHeight = pickerHeight,
                contentPadding = contentPadding,
                listState = monthListState,
                centeredIndex = centeredMonthIndex,
                label = { "${it + 1}월" },
                onClick = { onMonthClick(it + 1) },
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    SelectButton(onClick = onConfirm)
}

@Composable
private fun BoxScope.SelectionBand(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.Center)
            .height(ITEM_HEIGHT_DP.dp)
            .background(Sd800, RoundedCornerShape(10.dp))
    )
}

@Composable
private fun RowScope.PickerColumn(
    itemCount: Int,
    pickerHeight: Dp,
    contentPadding: PaddingValues,
    listState: LazyListState,
    centeredIndex: Int,
    label: (Int) -> String,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState, snapPosition = SnapPosition.Center)
    LaunchedEffect(listState, itemCount) {
        snapPickerToCenterWhenScrollEnds(listState, itemCount)
    }
    LazyColumn(
        modifier = modifier
            .weight(1f)
            .height(pickerHeight),
        state = listState,
        flingBehavior = flingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        items(itemCount) { index ->
            val isInSelectionBand = index == centeredIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ITEM_HEIGHT_DP.dp)
                    .clickable { onClick(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label(index),
                    color = if (isInSelectionBand) White else Gray700,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun SelectButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = PrimBase),
        shape = CircleShape
    ) {
        Text(
            text = stringResource(string.home_button_select),
            style = hd16,
        )
    }
}

@Composable
private fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Image(
        modifier = modifier
            .clickable { onClick() },
        painter = painterResource(drawable.ic_close),
        contentDescription = ""
    )
}

@Composable
private fun MonthWeekDaysHeader(
    locale: Locale,
    firstDayOfWeek: DayOfWeek,
    colors: CalendarColors,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = remember(firstDayOfWeek) { daysOfWeek(firstDayOfWeek = firstDayOfWeek) }

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, locale).uppercase(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = colors.weekdayText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MonthDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    photoCount: Int,
    photoUrls: List<String>,
    colors: CalendarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        NeonStyleDay(
            modifier = Modifier,
            topText = day.date.dayOfMonth.toString(),
            isSelected = isSelected,
            showDot = photoCount > 0,
            content = {
                MonthDayThumbnailBox(
                    isSelected = isSelected,
                    colors = colors,
                    photoUrls = photoUrls,
                )
            }
        )
    }
}

@Composable
private fun MonthDayThumbnailBox(
    isSelected: Boolean,
    colors: CalendarColors,
    photoUrls: List<String>,
) {
    val dashedBorderColor = if (isSelected) Gray900 else colors.weekdayText
    val showDashedBorder = photoUrls.isEmpty()
    Box(
        modifier = Modifier
            .padding(top = 6.dp)
            .requiredSize(32.dp)
            .clip(MonthDayCellCornerShape)
            .background(if (isSelected) colors.selectedInnerBox else Transparent)
            .then(
                if (showDashedBorder) Modifier.monthDayDashedBorder(dashedBorderColor)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        MonthDayThumbnails(photoUrls = photoUrls)
    }
}

private fun Modifier.monthDayDashedBorder(color: androidx.compose.ui.graphics.Color) = drawBehind {
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                cornerRadius = CornerRadius(4.dp.toPx()),
            )
        )
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(3.dp.toPx(), 2.dp.toPx()),
                phase = 0f,
            ),
        ),
    )
}

@Composable
private fun MonthDayThumbnails(photoUrls: List<String>) {
    when (photoUrls.size) {
        1 -> SingleDayThumbnail(url = photoUrls[0])
        2 -> TwoDayThumbnails(urls = photoUrls)
        else -> { /* pic=0: 빈 셀 */ }
    }
}

@Composable
private fun SingleDayThumbnail(url: String) {
    MonthDayThumbnailImage(
        url = url,
        modifier = Modifier
            .size(20.dp, 26.dp)
            .clip(MonthDayCellCornerShape)
            .border(1.dp, White, MonthDayCellCornerShape),
    )
}

@Composable
private fun MonthDayThumbnailImage(
    url: String,
    modifier: Modifier = Modifier,
) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
}

private val MonthDayThumbnailSize = 20.dp

@Composable
private fun TwoDayThumbnails(urls: List<String>) {
    if (urls.size < 2) return
    Box(modifier = Modifier.fillMaxSize()) {
        MonthDayThumbnailImage(
            url = urls[0],
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (2).dp, y = (3).dp)
                .rotate(-10f)
                .size(20.dp, 26.dp)
                .clip(MonthDayCellCornerShape)
                .border(1.dp, White, MonthDayCellCornerShape),
        )
        MonthDayThumbnailImage(
            url = urls[1],
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-1).dp, y = (-1).dp)
                .rotate(4f)
                .size(20.dp, 26.dp)
                .clip(MonthDayCellCornerShape)
                .border(1.dp, White, MonthDayCellCornerShape),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF1E1D25)
@Composable
private fun MonthSelectBottomSheetPreview() {
    MonthSelectBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = {},
        currentYearMonth = YearMonth.now(),
        locale = Locale.getDefault(),
        colors = calendarColors(),
        onMonthSelected = {}
    )
}

@Preview
@Composable
private fun MonthlyCalendarPreview() {
    val today = LocalDate.now()
    val state = rememberMonthCalendarState(
        selectedDate = today,
        firstDayOfWeek = DayOfWeek.SUNDAY
    )
    val placeholderUri = "https://picsum.photos/200/300" // Example URL for a 200x300 image

    MonthlyCalendar(
        calendarState = state,
        selectedDate = today,
        onDateSelected = {},
        photoCountByDate = mapOf(
            today to 3,
            today.minusDays(2) to 1
        ),
        photoUrlsByDate = mapOf(
            today to listOf(placeholderUri, placeholderUri),
            today.minusDays(2) to listOf(placeholderUri),
        ),
    )
}
