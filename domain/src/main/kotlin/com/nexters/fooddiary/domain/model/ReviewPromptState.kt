package com.nexters.fooddiary.domain.model

data class ReviewPromptState(
    val successfulRecordCount: Int = 0,
    val hasRequestedReview: Boolean = false,
)
