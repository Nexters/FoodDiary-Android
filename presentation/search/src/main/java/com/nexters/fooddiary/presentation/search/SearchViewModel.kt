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
    val errorMessage: String? = null,
) : MavericksState

class SearchViewModel @AssistedInject constructor(
    @Assisted initialState: SearchScreenState,
    private val searchRestaurantsUseCase: SearchRestaurantsUseCase,
) : MavericksViewModel<SearchScreenState>(initialState) {

    private var searchJob: Job? = null

    fun onKeywordChanged(diaryId: Long?, keyword: String) {
        setState {
            copy(
                keyword = keyword,
                errorMessage = null,
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

    private fun scheduleSearch(
        diaryId: Long?,
        keyword: String?,
        immediate: Boolean,
    ) {
        searchJob?.cancel()

        if (diaryId == null && keyword.isNullOrBlank()) {
            setState {
                copy(
                    restaurants = emptyList(),
                    totalCount = 0,
                    isLoading = false,
                    errorMessage = null,
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
        setState { copy(isLoading = true, errorMessage = null) }

        val result =
            searchRestaurantsUseCase(
                diaryId = diaryId,
                keyword = keyword,
                page = DefaultPage,
                size = DefaultSize,
            )
        setState {
            copy(
                restaurants = result.restaurants,
                totalCount = result.totalCount,
                page = result.page,
                size = result.size,
                isEnd = result.isEnd,
                isLoading = false,
                errorMessage = null,
            )
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SearchViewModel, SearchScreenState> {
        override fun create(state: SearchScreenState): SearchViewModel
    }

    companion object : MavericksViewModelFactory<SearchViewModel, SearchScreenState> by hiltMavericksViewModelFactory()
}
