package com.nexters.fooddiary.data.remote.auth

import com.nexters.fooddiary.data.remote.auth.model.request.LoginRequest
import com.nexters.fooddiary.data.remote.auth.model.request.UpdateDeviceRequest
import com.nexters.fooddiary.data.remote.auth.model.response.LoginResponse
import com.nexters.fooddiary.data.remote.auth.model.response.VerifyTokenResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/members/me/device")
    suspend fun updateMyDevice(@Body request: UpdateDeviceRequest)

    @GET("/auth/verify")
    suspend fun verifyToken(): VerifyTokenResponse

    @DELETE("/users/me")
    suspend fun deleteMe()
}
