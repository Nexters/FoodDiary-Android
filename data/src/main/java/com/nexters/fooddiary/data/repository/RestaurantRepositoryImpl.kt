package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.toDomainModel
import com.nexters.fooddiary.data.remote.restaurant.RestaurantApi
import com.nexters.fooddiary.domain.model.RestaurantSearchResult
import com.nexters.fooddiary.domain.repository.RestaurantRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantApi: RestaurantApi,
) : RestaurantRepository {

    override suspend fun searchRestaurants(
        diaryId: Long?,
        keyword: String?,
        page: Int,
        size: Int,
    ): RestaurantSearchResult {
        return restaurantApi.searchRestaurant(
            diaryId = diaryId,
            keyword = keyword,
            page = page,
            size = size,
        ).toDomainModel()
    }
}
