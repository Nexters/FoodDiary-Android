package com.nexters.fooddiary.data.remote.restaurant.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantSearchResponse(
    @SerialName("restaurants")
    val restaurants: List<RestaurantResponseItem> = emptyList(),
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("is_end")
    val isEnd: Boolean,
)

@Serializable
data class RestaurantResponseItem(
    @SerialName("name")
    val name: String,
    @SerialName("address_name")
    val addressName: String? = null,
    @SerialName("road_address")
    val roadAddress: String,
    @SerialName("url")
    val url: String,
)
