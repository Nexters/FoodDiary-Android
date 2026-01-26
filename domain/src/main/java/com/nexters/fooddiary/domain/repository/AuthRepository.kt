package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.User

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    fun getCurrentUser(): User?
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
}
