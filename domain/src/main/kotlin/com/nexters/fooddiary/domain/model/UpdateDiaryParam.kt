package com.nexters.fooddiary.domain.model

data class UpdateDiaryParam(
    val category: String? = null,
    val restaurantName: String? = null,
    val restaurantUrl: String? = null,
    val roadAddress: String? = null,
    val tags: List<String>? = null,
    val note: String? = null,
    val coverPhotoId: Int? = null,
    val photoIds: List<Int>? = null,
)
