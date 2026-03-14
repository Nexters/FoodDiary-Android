package com.nexters.fooddiary.data.mapper

import com.nexters.fooddiary.data.remote.restaurant.model.response.RestaurantResponseItem
import com.nexters.fooddiary.data.remote.restaurant.model.response.RestaurantSearchResponse
import com.nexters.fooddiary.domain.model.RestaurantItem
import com.nexters.fooddiary.domain.model.RestaurantSearchResult

internal fun RestaurantResponseItem.toDomainModel(): RestaurantItem {
    return RestaurantItem(
        name = name,
        addressName = addressName,
        roadAddress = roadAddress,
        url = url,
    )
}

internal fun RestaurantSearchResponse.toDomainModel(): RestaurantSearchResult {
    return RestaurantSearchResult(
        restaurants = restaurants.map { it.toDomainModel() },
        totalCount = totalCount,
        page = page,
        size = size,
        isEnd = isEnd,
    )
}
