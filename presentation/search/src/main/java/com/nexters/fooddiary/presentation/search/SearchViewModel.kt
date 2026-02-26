package com.nexters.fooddiary.presentation.search

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.model.RestaurantItem
import com.nexters.fooddiary.domain.usecase.SearchRestaurantsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SearchDebounceMillis = 300L
private const val DefaultPage = 1
private const val DefaultSize = 15

data class SearchScreenState(
    val keyword: String = "",
    val restaurants: List<RestaurantItem> = emptyList(),
    val totalCount: Int = 0,
    val page: Int = 1,
    val size: Int = 15,
    val isEnd: Boolean = true,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val loadMoreErrorMessage: String? = null,
) : MavericksState

class SearchViewModel @AssistedInject constructor(
    @Assisted initialState: SearchScreenState,
    private val searchRestaurantsUseCase: SearchRestaurantsUseCase,
) : MavericksViewModel<SearchScreenState>(initialState) {

    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null
    private var lastDiaryId: Long? = null
    private var lastKeyword: String? = null

    fun onKeywordChanged(diaryId: Long?, keyword: String) {
        setState {
            copy(
                keyword = keyword,
                errorMessage = null,
                loadMoreErrorMessage = null,
            )
        }
        scheduleSearch(
            diaryId = diaryId,
            keyword = keyword,
            immediate = false,
        )
    }

    fun loadInitialRecommendations(diaryId: Long?) {
        if (diaryId == null) return
        scheduleSearch(
            diaryId = diaryId,
            keyword = null,
            immediate = true,
        )
    }

    fun searchByKeywordImmediately(
        diaryId: Long?,
        keyword: String,
    ) {
        setState {
            copy(
                keyword = keyword,
                errorMessage = null,
                loadMoreErrorMessage = null,
            )
        }
        scheduleSearch(
            diaryId = diaryId,
            keyword = keyword,
            immediate = true,
        )
    }

    fun loadNextPage() = requestNextPage(allowOnError = false)

    fun retryLoadMore() = requestNextPage(allowOnError = true)

    private fun requestNextPage(allowOnError: Boolean) = withState { state ->
        if (state.isLoading || state.isLoadingMore || state.isEnd || state.restaurants.isEmpty() || loadMoreJob?.isActive == true) {
            return@withState
        }
        if (!allowOnError && !state.loadMoreErrorMessage.isNullOrBlank()) {
            return@withState
        }

        val diaryId = lastDiaryId
        val keyword = lastKeyword
        if (diaryId == null && keyword.isNullOrBlank()) {
            return@withState
        }

        loadMoreJob = viewModelScope.launch {
            fetchNextPage(
                diaryId = diaryId,
                keyword = keyword,
                nextPage = state.page + 1,
                size = state.size,
            )
        }
    }

    private fun scheduleSearch(
        diaryId: Long?,
        keyword: String?,
        immediate: Boolean,
    ) {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        if (diaryId == null && keyword.isNullOrBlank()) {
            setState {
                copy(
                    restaurants = emptyList(),
                    totalCount = 0,
                    page = DefaultPage,
                    size = DefaultSize,
                    isEnd = true,
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = null,
                    loadMoreErrorMessage = null,
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            if (!immediate) {
                delay(SearchDebounceMillis)
            }
            fetchRestaurants(
                diaryId = diaryId,
                keyword = keyword?.takeIf { it.isNotBlank() },
            )
        }
    }

    private suspend fun fetchRestaurants(
        diaryId: Long?,
        keyword: String?,
    ) {
        setState {
            copy(
                isLoading = true,
                isLoadingMore = false,
                errorMessage = null,
                loadMoreErrorMessage = null,
            )
        }

        runCatching {
            searchRestaurantsUseCase(
                diaryId = diaryId,
                keyword = keyword,
                page = DefaultPage,
                size = DefaultSize,
            )
        }.onSuccess { result ->
            lastDiaryId = diaryId
            lastKeyword = keyword
            setState {
                copy(
                    restaurants = result.restaurants,
                    totalCount = result.totalCount,
                    page = result.page,
                    size = result.size,
                    isEnd = result.isEnd,
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = null,
                    loadMoreErrorMessage = null,
                )
            }
        }.onFailure { throwable ->
            if (throwable is CancellationException) throw throwable
            setState {
                copy(
                    restaurants = emptyList(),
                    totalCount = 0,
                    page = DefaultPage,
                    size = DefaultSize,
                    isEnd = true,
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = throwable.message ?: "search_failed",
                    loadMoreErrorMessage = null,
                )
            }
        }
    }

    private suspend fun fetchNextPage(
        diaryId: Long?,
        keyword: String?,
        nextPage: Int,
        size: Int,
    ) {
        setState {
            copy(
                isLoadingMore = true,
                loadMoreErrorMessage = null,
            )
        }

        runCatching {
            searchRestaurantsUseCase(
                diaryId = diaryId,
                keyword = keyword,
                page = nextPage,
                size = size,
            )
        }.onSuccess { result ->
            setState {
                copy(
                    restaurants = restaurants + result.restaurants,
                    totalCount = result.totalCount,
                    page = result.page,
                    size = result.size,
                    isEnd = result.isEnd,
                    isLoadingMore = false,
                    loadMoreErrorMessage = null,
                )
            }
        }.onFailure { throwable ->
            if (throwable is CancellationException) throw throwable
            setState {
                copy(
                    isLoadingMore = false,
                    loadMoreErrorMessage = "search_load_more_error",
                )
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SearchViewModel, SearchScreenState> {
        override fun create(state: SearchScreenState): SearchViewModel
    }

    companion object : MavericksViewModelFactory<SearchViewModel, SearchScreenState> by hiltMavericksViewModelFactory()
}
