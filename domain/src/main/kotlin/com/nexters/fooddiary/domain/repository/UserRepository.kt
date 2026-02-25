package com.nexters.fooddiary.domain.repository

interface UserRepository {
    suspend fun getMe(): Result<String>
}
