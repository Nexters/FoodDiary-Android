package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.datasource.remote.AuthRemoteDataSource
import com.nexters.fooddiary.data.mapper.toDomain
import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import com.nexters.fooddiary.domain.model.AuthInfo
import com.nexters.fooddiary.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun login(provider: String, idToken: String): Result<AuthInfo>{
        return runCatching { //TODO 예외처리 컨벤션 정립 필요
            authRemoteDataSource.login(LoginRequest(provider, idToken))
        }.map { response ->
            response.toDomain()
        }
    }
}
