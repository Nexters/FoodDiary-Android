package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.AuthInfo

interface AuthRepository {
    suspend fun login(provider: String, idToken: String): Result<AuthInfo>
}
