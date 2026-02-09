package com.nexters.fooddiary.data.remote.diary.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryPhoto(
    @SerialName("photo_id")
    val photoId: Long,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("taken_at")
    val takenAt: String,
    @SerialName("restaurant_name")
    val restaurantName: String?,
    @SerialName("menu_name")
    val menuName: String?,
    @SerialName("menu_price")
    val menuPrice: Int?,
    @SerialName("time_type")
    val timeType: String?
)
