package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.local.TokenStore
import com.nexters.fooddiary.data.remote.user.UserApi
import com.nexters.fooddiary.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenStore: TokenStore,
) : UserRepository {

    override suspend fun getMe(): Result<String> {
        return runCatching {
            tokenStore.getCachedNickname()
                ?: tokenStore.getNickname()
                ?: userApi.getMe().name.also { name ->
                    tokenStore.saveNickname(name)
                }
        }
    }
}
