package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateDiaryRequest(
    @SerialName("category")
    val category: String? = null,
    @SerialName("restaurant_name")
    val restaurantName: String? = null,
    @SerialName("restaurant_url")
    val restaurantUrl: String? = null,
    @SerialName("road_address")
    val roadAddress: String? = null,
    @SerialName("tags")
    val tags: List<String>? = null,
    @SerialName("note")
    val note: String? = null,
    @SerialName("cover_photo_id")
    val coverPhotoId: Int? = null,
    @SerialName("photo_ids")
    val photoIds: List<Int>? = null,
)
