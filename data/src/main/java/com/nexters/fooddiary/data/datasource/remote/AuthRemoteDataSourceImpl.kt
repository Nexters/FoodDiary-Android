package com.nexters.fooddiary.data.datasource.remote

import com.nexters.fooddiary.data.remote.auth.AuthApi
import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import com.nexters.fooddiary.data.remote.auth.model.response.LoginResponse
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRemoteDataSource {
    override suspend fun login(request: LoginRequest): LoginResponse {
        return authApi.login(request)
    }
}
