package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.RestaurantSearchResult

interface RestaurantRepository {
    suspend fun searchRestaurants(
        diaryId: Long?,
        keyword: String?,
        page: Int,
        size: Int,
    ): RestaurantSearchResult
}
