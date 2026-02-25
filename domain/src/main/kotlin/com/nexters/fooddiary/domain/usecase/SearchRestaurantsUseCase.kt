package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.RestaurantSearchResult
import com.nexters.fooddiary.domain.repository.RestaurantRepository
import javax.inject.Inject

class SearchRestaurantsUseCase @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
) {
    suspend operator fun invoke(
        diaryId: Long?,
        keyword: String?,
        page: Int = 1,
        size: Int = 15,
    ): RestaurantSearchResult {
        return restaurantRepository.searchRestaurants(
            diaryId = diaryId,
            keyword = keyword,
            page = page,
            size = size,
        )
    }
}
