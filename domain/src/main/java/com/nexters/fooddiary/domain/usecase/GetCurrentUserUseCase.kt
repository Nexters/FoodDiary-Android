package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.User
import com.nexters.fooddiary.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
