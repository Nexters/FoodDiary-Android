package com.nexters.fooddiary.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.R
import com.nexters.fooddiary.core.ui.component.AddPhotoBox
import com.nexters.fooddiary.core.ui.component.DailyHeader
import com.nexters.fooddiary.core.ui.food.FoodImageCard
import com.nexters.fooddiary.core.ui.food.FoodImageState
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.PretendardFontFamily
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.White
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import java.time.LocalDate

@Composable
internal fun DetailScreen(
    viewModel: DetailViewModel = mavericksViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.collectAsState()

    DetailContent(
        selectedDateString = state.selectedDateString,
        dailyMeals = state.dailyMeals,
        onPreviousDay = viewModel::navigateToPreviousDay,
        onNextDay = viewModel::navigateToNextDay,
        onMealCardClick = viewModel::onMealCardClick,
        onEditClick = viewModel::onEditClick,
        onSaveClick = viewModel::onSaveClick,
        onShareClick = viewModel::onShareClick,
    )
}

@Composable
private fun DetailContent(
    selectedDateString: String,
    dailyMeals: Map<String, List<MealUiModel>>,  // Key: ISO-8601 date string
    onPreviousDay: () -> Unit = {},
    onNextDay: () -> Unit = {},
    onMealCardClick: (String) -> Unit = {},
    onEditClick: (String, String) -> Unit = { _, _ -> }, // (mealType, dateString)
    onSaveClick: (String) -> Unit = {},
    onShareClick: (String) -> Unit = {},
) {
    // 선택된 날짜의 식사 가져오기 (없으면 기본 슬롯 생성)
    val meals = dailyMeals[selectedDateString] ?: createDefaultMeals(selectedDateString)
    val date = LocalDate.parse(selectedDateString)
    val hazeState = rememberHazeState()

    Scaffold(
        containerColor = SdBase,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            stickyHeader(key = selectedDateString) {
                DailyHeader(
                    date = date,
                    onPreviousDay = onPreviousDay,
                    onNextDay = onNextDay,
                    hazeState = hazeState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp)
                )
            }

            items(meals, key = { it.id }) { meal ->
                MealSection(
                    meal = meal,
                    onCardClick = { onMealCardClick(meal.id) },
                    onEditClick = { onEditClick(meal.mealType, meal.dateString) },
                    onSaveClick = { onSaveClick(meal.id) },
                    onShareClick = { onShareClick(meal.id) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealSection(
    meal: MealUiModel,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
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
                text = meal.mealType,
                style = AppTypography.p18,
                color = White,
                fontFamily = PretendardFontFamily,
            )

            // FullUI 상태일 때만 수정 버튼 표시
            if (!meal.isEmpty && !meal.isPending) {
                Text(
                    text = "수정",
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
                    onAddPhoto = onCardClick
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
                    category = meal.category,
                    keywords = meal.keywords,
                    onSaveClick = onSaveClick,
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
                category = meal.category,
                keywords = meal.keywords,
                onSaveClick = onSaveClick,
                onShareClick = onShareClick,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealInfoSection(
    place: String,
    category: String,
    keywords: List<String>,
    onSaveClick: () -> Unit,
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
                    modifier = Modifier.clickable { onSaveClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // 아이콘과 텍스트 사이 간격
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = "복사",
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "복사",
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
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = "공유",
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "공유",
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


@Preview(showBackground = true, backgroundColor = 0xFF191821)
@Composable
private fun DetailScreenPreview() {
    val today = LocalDate.now()
    val mockMeals = mapOf(
        today.toString() to listOf(
            MealUiModel(
                id = "1",
                dateString = today.toString(),
                mealType = "아침",
                time = "07:00",
                location = "마포구",
                place = "호진이네",
                category = "중식",
                keywords = listOf("#양장피", "#어향동고"),
                imageUrls = listOf("https://picsum.photos/300"),
                isEmpty = false,
                isPending = false,
            ),
            MealUiModel(
                id = "2",
                dateString = today.toString(),
                mealType = "점심",
                time = "12:30",
                location = "강남구",
                place = "",
                category = "",
                keywords = emptyList(),
                imageUrls = listOf("https://picsum.photos/300"),
                isEmpty = false,
                isPending = true,
            ),
            MealUiModel(
                id = "3",
                dateString = today.toString(),
                mealType = "저녁",
                time = "19:00",
                location = "",
                place = "",
                category = "",
                keywords = emptyList(),
                imageUrls = emptyList(),
                isEmpty = true,
                isPending = false,
            ),
        )
    )

    DetailContent(
        selectedDateString = today.toString(),
        dailyMeals = mockMeals,
    )
}

private fun createDefaultMeals(dateString: String): List<MealUiModel> {
    return listOf(
        MealUiModel(
            id = "${dateString}_breakfast",
            dateString = dateString,
            mealType = "아침",
            time = "",
            location = "",
            place = "",
            category = "",
            keywords = emptyList(),
            imageUrls = emptyList(),
            isEmpty = true,
            isPending = false,
        ),
        MealUiModel(
            id = "${dateString}_lunch",
            dateString = dateString,
            mealType = "점심",
            time = "",
            location = "",
            place = "",
            category = "",
            keywords = emptyList(),
            imageUrls = emptyList(),
            isEmpty = true,
            isPending = false,
        ),
        MealUiModel(
            id = "${dateString}_dinner",
            dateString = dateString,
            mealType = "저녁",
            time = "",
            location = "",
            place = "",
            category = "",
            keywords = emptyList(),
            imageUrls = emptyList(),
            isEmpty = true,
            isPending = false,
        ),
    )
}
