package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.deleteAccount()
    }
}

