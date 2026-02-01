package com.nexters.fooddiary.data.remote.auth

import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import com.nexters.fooddiary.data.remote.auth.model.response.LoginResponse
import com.nexters.fooddiary.data.remote.auth.model.response.VerifyTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/auth/verify")
    suspend fun verifyToken(): VerifyTokenResponse
}
