package com.nexters.fooddiary.domain.model

/**
 * 미디어 아이템 Domain 모델
 */
data class MediaItem(
    val uri: String,
    val dateTaken: Long  //초 단위 (epoch time)
)
