package com.nexters.fooddiary.data.datasource.remote

import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import com.nexters.fooddiary.data.remote.auth.model.response.LoginResponse

interface AuthRemoteDataSource {
    suspend fun login(request: LoginRequest): LoginResponse
}
