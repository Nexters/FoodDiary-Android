package com.nexters.fooddiary.data.remote.photo.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoFinalRecordResponse(
    @SerialName("restaurant_name")
    val restaurantName: String,
    @SerialName("menu_name")
    val menuName: String,
    @SerialName("menu_price")
    val menuPrice: Int,
    @SerialName("time_type")
    val timeType: String
)
