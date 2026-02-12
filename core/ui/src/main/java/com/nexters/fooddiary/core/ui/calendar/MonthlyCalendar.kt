package com.nexters.fooddiary.core.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.nexters.fooddiary.core.ui.calendar.theme.CalendarColors
import com.nexters.fooddiary.core.ui.calendar.theme.calendarColors
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.Gray900
import com.nexters.fooddiary.core.ui.theme.Sd900
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
            locale = locale,
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
    locale: Locale,
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
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Sd900,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            (1..12).forEach { monthValue ->
                key(monthValue) {
                    val yearMonth = YearMonth.of(currentYearMonth.year, monthValue)
                    Text(
                        text = "${monthValue}월",
                        color = colors.headerText,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMonthSelected(yearMonth) }
                            .padding(vertical = 12.dp),
                    )
                }
            }
        }
    }
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
            showDot = false,
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
