package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.PrimBase

@Composable
fun BarChartCard(
    title: String,
    descriptionPrefix: String,
    highlightText: String,
    descriptionSuffix: String,
    bars: List<BarChartItem>,
    modifier: Modifier = Modifier,
    barSpacing: Dp = BarChartCardDefaults.CompareBarSpacing,
    chartHeight: Dp = BarChartCardDefaults.ChartHeight,
) {
    val titleStyle = BarChartTitleTextStyle
    val highlightStyle = titleStyle.copy(color = PrimBase)

    BaseBarChartCard(
        bars = bars,
        modifier = modifier,
        barSpacing = barSpacing,
        chartHeight = chartHeight,
        headerContent = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = titleStyle,
                    color = Gray050,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Gray200, fontSize = 10.sp)) {
                            append(descriptionPrefix)
                        }
                        withStyle(
                            SpanStyle(
                                color = highlightStyle.color,
                                fontSize = highlightStyle.fontSize,
                                fontWeight = highlightStyle.fontWeight,
                                letterSpacing = highlightStyle.letterSpacing,
                            )
                        ) {
                            append(highlightText)
                        }
                        withStyle(SpanStyle(color = Gray200, fontSize = 10.sp)) {
                            append(descriptionSuffix)
                        }
                    },
                    style = BarChartLabelTextStyle,
                )
            }
        },
    )
}

@Composable
fun HighlightedSubjectBarChartCard(
    title: String,
    description: String,
    highlightPrefixText: String,
    highlightedText: String,
    bars: List<BarChartItem>,
    modifier: Modifier = Modifier,
    barSpacing: Dp = BarChartCardDefaults.FrequentBarSpacing,
    chartHeight: Dp = BarChartCardDefaults.ChartHeight,
) {
    val titleStyle = BarChartTitleTextStyle

    BaseBarChartCard(
        bars = bars,
        modifier = modifier,
        barSpacing = barSpacing,
        chartHeight = chartHeight,
        headerContent = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = title,
                        style = titleStyle,
                        color = Gray050,
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Gray050)) {
                                append(highlightPrefixText)
                            }
                            withStyle(SpanStyle(color = PrimBase)) {
                                append(highlightedText)
                            }
                        },
                        style = titleStyle,
                    )
                }
                Text(
                    text = description,
                    style = BarChartLabelTextStyle,
                    color = Gray200,
                )
            }
        },
    )
}

@Preview
@Composable
private fun BarChartCardPreview() {
    FoodDiaryTheme {
        BarChartCard(
            title = "먹기 전에\n카메라부터 찾았네요.",
            descriptionPrefix = "지난 달 대비 기록된 사진이 ",
            highlightText = "70%",
            descriptionSuffix = " 증가했어요.",
            bars = listOf(
                BarChartItem(
                    label = "1월",
                    percentage = 20f,
                    valueText = "20",
                    topColor = Color(0xFF415199),
                    bottomColor = Color(0xFF8AA6E6),
                ),
                BarChartItem(
                    label = "2월",
                    percentage = 100f,
                    valueText = "140",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
            ).withStaggeredAnimation(delayStepMillis = 90),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun HighlightedSubjectBarChartCardPreview() {
    FoodDiaryTheme {
        HighlightedSubjectBarChartCard(
            title = "가장 자주 먹은",
            description = "고민은 길었고, 메뉴는 늘 비슷했어요.",
            highlightPrefixText = "음식은 ",
            highlightedText = "마라샹궈",
            bars = listOf(
                BarChartItem(
                    label = "1주차",
                    percentage = 50f,
                    valueText = "3회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarChartItem(
                    label = "2주차",
                    percentage = 60f,
                    valueText = "4회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarChartItem(
                    label = "3주차",
                    percentage = 10f,
                    valueText = "2회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarChartItem(
                    label = "4주차",
                    percentage = 90f,
                    valueText = "5회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
                BarChartItem(
                    label = "5주차",
                    percentage = 100f,
                    valueText = "6회",
                    topColor = Color(0xFFFE670E),
                    bottomColor = Color(0xFFFFB183),
                ),
            ).withStaggeredAnimation(delayStepMillis = 70),
            modifier = Modifier.padding(16.dp),
        )
    }
}
