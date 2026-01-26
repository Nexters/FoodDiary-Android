package com.nexters.fooddiary.domain.model

data class AuthInfo(
    val userId: String,
    val accessToken: String,
    val isFirst: Boolean
)
