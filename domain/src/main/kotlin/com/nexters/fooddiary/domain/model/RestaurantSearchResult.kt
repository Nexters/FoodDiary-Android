package com.nexters.fooddiary.domain.model

data class RestaurantItem(
    val name: String,
    val roadAddress: String,
    val url: String,
)

data class RestaurantSearchResult(
    val restaurants: List<RestaurantItem>,
    val totalCount: Int,
    val page: Int,
    val size: Int,
    val isEnd: Boolean,
)
