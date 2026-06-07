package com.nexters.fooddiary.domain.model

data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isFirst: Boolean,
)

