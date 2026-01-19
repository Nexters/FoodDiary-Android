package com.nexters.fooddiary.domain.model

/**
 * 미디어 아이템 Domain 모델
 */
data class MediaItem(
    val uri: String,
    val displayName: String,
    val dateTaken: Long
)
