package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.AuthRepository
import javax.inject.Inject

class SyncDeviceTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(deviceToken: String): Result<Unit> {
        return authRepository.syncDeviceToken(deviceToken)
    }
}
