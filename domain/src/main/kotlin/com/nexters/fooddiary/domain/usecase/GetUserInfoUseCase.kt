package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<String> {
        return userRepository.getMe()
    }
}
