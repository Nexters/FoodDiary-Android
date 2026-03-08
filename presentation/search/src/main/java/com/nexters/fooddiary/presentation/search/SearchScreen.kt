package com.nexters.fooddiary.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.domain.model.RestaurantItem

@Composable
internal fun SearchScreen(
    diaryId: Long?,
    initialKeyword: String?,
    onClose: () -> Unit,
    onSelectRestaurant: (RestaurantItem) -> Unit = {},
    viewModel: SearchViewModel = mavericksViewModel(),
) {
    val state by viewModel.collectAsStateWithLifecycle()

    LaunchedEffect(diaryId, initialKeyword) {
        if (initialKeyword.isNullOrBlank()) {
            viewModel.loadInitialRecommendations(diaryId)
        } else {
            viewModel.searchByKeywordImmediately(
                diaryId = diaryId,
                keyword = initialKeyword,
            )
        }
    }

    SearchScreen(
        state = state,
        onClose = onClose,
        onKeywordChanged = { keyword -> viewModel.onKeywordChanged(diaryId, keyword) },
        onSelectRestaurant = onSelectRestaurant,
        onLoadNextPage = viewModel::loadNextPage,
        onRetryLoadMore = viewModel::retryLoadMore,
    )
}

@Composable
private fun SearchScreen(
    state: SearchScreenState,
    onClose: () -> Unit,
    onKeywordChanged: (String) -> Unit,
    onSelectRestaurant: (RestaurantItem) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryLoadMore: () -> Unit,
) {
    val hasKeyword = state.keyword.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SdBase)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.search_close),
                    tint = Gray050,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SearchInputField(
            keyword = state.keyword,
            isActive = hasKeyword,
            onKeywordChanged = onKeywordChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(43.dp),
        )

        if (hasKeyword) {
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.search_guide_title),
                style = AppTypography.p14,
                color = Gray050,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Gray050)
                }
            }

            state.restaurants.isEmpty() -> {
                val emptyMessage = if (state.errorMessage.isNullOrBlank()) {
                    stringResource(R.string.search_empty)
                } else {
                    stringResource(R.string.search_error)
                }
                Text(
                    text = emptyMessage,
                    style = AppTypography.p14,
                    color = Gray400,
                )
            }

            else -> {
                RestaurantListCard(
                    restaurants = state.restaurants,
                    isEnd = state.isEnd,
                    isLoading = state.isLoading,
                    isLoadingMore = state.isLoadingMore,
                    loadMoreErrorMessage = state.loadMoreErrorMessage,
                    onSelectRestaurant = onSelectRestaurant,
                    onLoadNextPage = onLoadNextPage,
                    onRetryLoadMore = onRetryLoadMore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                )
            }
        }
    }
}

@Composable
private fun SearchInputField(
    keyword: String,
    isActive: Boolean,
    onKeywordChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = keyword,
        onValueChange = onKeywordChanged,
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isActive) PrimBase else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
            )
            .clip(RoundedCornerShape(10.dp))
            .background(Sd900)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        singleLine = true,
        textStyle = AppTypography.p15.copy(color = Gray050),
        cursorBrush = SolidColor(Gray050),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (keyword.isBlank()) {
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            style = AppTypography.p15,
                            color = Gray600,
                        )
                    }
                    innerTextField()
                }
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Gray050,
                    modifier = Modifier.size(18.dp),
                )
            }
        },
        interactionSource = interactionSource,
    )
}

@Composable
private fun RestaurantListCard(
    restaurants: List<RestaurantItem>,
    isEnd: Boolean,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    loadMoreErrorMessage: String?,
    onSelectRestaurant: (RestaurantItem) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val shouldLoadNextPage = !isEnd && !isLoading && !isLoadingMore && restaurants.isNotEmpty()

    LaunchedEffect(listState, shouldLoadNextPage, restaurants.size) {
        if (!shouldLoadNextPage) return@LaunchedEffect

        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@collect
                if (lastVisibleItemIndex >= layoutInfo.totalItemsCount - 2) {
                    onLoadNextPage()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .border(1.dp, Sd800, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .padding(start = 20.dp, end = 20.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        itemsIndexed(
            items = restaurants,
            key = { index, restaurant -> "${restaurant.url}_$index" },
        ) { index, restaurant ->
            RestaurantRow(
                restaurant = restaurant,
                onSelect = { onSelectRestaurant(restaurant) },
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (index != restaurants.lastIndex) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Sd800),
                )
            }
        }

        if (isLoadingMore) {
            item(key = "load_more_progress") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = Gray050,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }

        if (!loadMoreErrorMessage.isNullOrBlank()) {
            item(key = "load_more_error") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.search_load_more_error),
                        style = AppTypography.p12,
                        color = Gray400,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.search_load_more_retry),
                        style = AppTypography.p12,
                        color = Gray050,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(onClick = onRetryLoadMore),
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantRow(
    restaurant: RestaurantItem,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = restaurant.name,
                style = AppTypography.p12,
                color = Gray050,
            )
            Text(
                text = restaurant.roadAddress,
                style = AppTypography.p12,
                color = Gray400,
            )
        }

        Text(
            text = stringResource(R.string.search_select),
            style = AppTypography.p12,
            color = Gray050,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onSelect),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun SearchScreenPreviewWithResults() {
    SearchScreen(
        state = SearchScreenState(
            keyword = "김밥",
            restaurants = listOf(
                RestaurantItem(
                    name = "김밥천국",
                    roadAddress = "서울특별시 성동구 광나루로 4가길 12-7 1층",
                    url = "https://place.map.kakao.com/1001",
                ),
                RestaurantItem(
                    name = "김밥지옥",
                    roadAddress = "서울특별시 성동구 광나루로 4가길 12-7 2층",
                    url = "https://place.map.kakao.com/1002",
                ),
                RestaurantItem(
                    name = "김밥나라",
                    roadAddress = "서울특별시 성동구 광나루로 4가길 12-7 3층",
                    url = "https://place.map.kakao.com/1003",
                ),
            ),
            totalCount = 3,
            isEnd = true,
        ),
        onClose = {},
        onKeywordChanged = {},
        onSelectRestaurant = {},
        onLoadNextPage = {},
        onRetryLoadMore = {},
    )
}
