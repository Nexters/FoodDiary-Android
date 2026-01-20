package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.User
import com.nexters.fooddiary.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return authRepository.signInWithGoogle(idToken)
    }
}
