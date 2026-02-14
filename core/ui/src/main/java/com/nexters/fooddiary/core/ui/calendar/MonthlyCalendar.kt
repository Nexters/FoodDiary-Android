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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Collections.emptyMap
import java.util.Locale

private val MonthDayCellCornerShape = RoundedCornerShape(4.dp)

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
) {
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = remember { derivedStateOf { calendarState.firstVisibleMonth.yearMonth } }
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
                        val photoCount = photoCountByDate[day.date] ?: 0
                        MonthDayCell(
                            day = day,
                            isSelected = day.date == selectedDate,
                            photoCount = photoCount,
                            colors = colors,
                            onClick = {
                                if (day.position != DayPosition.MonthDate) {
                                    coroutineScope.launch {
                                        calendarState.animateScrollToMonth(YearMonth.from(day.date))
                                        onDateSelected(day.date)
                                    }
                                } else {
                                    onDateSelected(day.date)
                                }
                            }
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
private const val YEAR_RANGE = 10

private fun LazyListLayoutInfo.centeredItemIndex(fallback: Int): Int =
    visibleItemsInfo.firstOrNull { it.covers(viewportCenterY) }?.index ?: fallback

private val LazyListLayoutInfo.viewportCenterY: Int
    get() = (viewportStartOffset + viewportEndOffset) / 2

private fun LazyListItemInfo.covers(y: Int): Boolean = y in offset until offset + size

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
    val startYear = YearMonth.now().year - YEAR_RANGE
    val years = remember(startYear) { (startYear..startYear + YEAR_RANGE * 2).toList() }

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

    val centeredYearIndex = yearListState.layoutInfo.centeredItemIndex((selectedYear - startYear).coerceIn(0, years.lastIndex))
    val centeredMonthIndex = monthListState.layoutInfo.centeredItemIndex((selectedMonth - 1).coerceIn(0, 11))

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
        PickerFadeOverlay()
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
private fun BoxScope.PickerFadeOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .matchParentSize()
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Sd900,
                        0.35f to Sd900,
                        0.4f to Transparent,
                        0.6f to Transparent,
                        0.65f to Sd900,
                        1f to Sd900,
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                )
            )
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
    LazyColumn(
        modifier = modifier
            .weight(1f)
            .height(pickerHeight),
        state = listState,
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
            modifier = Modifier.padding(14.dp),
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
                val dashedBorderColor = if (isSelected) Gray900 else colors.weekdayText
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .requiredSize(32.dp)
                        .clip(MonthDayCellCornerShape)
                        .background(
                            if (isSelected) colors.selectedInnerBox else Transparent
                        )
                        .drawBehind {
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
                                color = dashedBorderColor,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(3.dp.toPx(), 2.dp.toPx()),
                                        phase = 0f,
                                    ),
                                ),
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 이미지 추가 예정
                }
            }
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
    val state = rememberMonthCalendarState(
        selectedDate = LocalDate.now(),
        firstDayOfWeek = DayOfWeek.SUNDAY
    )

    MonthlyCalendar(
        calendarState = state,
        selectedDate = LocalDate.now(),
        onDateSelected = {},
        photoCountByDate = mapOf(
            LocalDate.now() to 3,
            LocalDate.now().minusDays(2) to 1
        )
    )
}
