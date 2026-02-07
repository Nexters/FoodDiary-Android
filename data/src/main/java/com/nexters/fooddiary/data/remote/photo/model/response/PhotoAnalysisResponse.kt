package com.nexters.fooddiary.data.remote.photo.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoAnalysisResponse(
    @SerialName("photo_id")
    val photoId: Long,
    @SerialName("food_category")
    val foodCategory: String?,
    @SerialName("restaurant_name_candidates")
    val restaurantNameCandidates: List<RestaurantCandidate>?,
    @SerialName("menu_candidates")
    val menuCandidates: List<MenuCandidate>?,
    @SerialName("keywords")
    val keywords: List<String>?
)

@Serializable
data class RestaurantCandidate(
    @SerialName("name")
    val name: String,
    @SerialName("confidence")
    val confidence: Double
)

@Serializable
data class MenuCandidate(
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int
)
