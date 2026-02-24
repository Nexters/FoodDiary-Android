package com.nexters.fooddiary.domain.repository

interface UserRepository {
    /**
     * 현재 사용자 닉네임을 반환한다.
     */
    suspend fun getMe(): Result<String>
}
