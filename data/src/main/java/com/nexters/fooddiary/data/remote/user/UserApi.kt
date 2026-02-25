package com.nexters.fooddiary.data.remote.user

import com.nexters.fooddiary.data.remote.user.model.UserMeResponse
import retrofit2.http.GET

interface UserApi {
    @GET("/users/me")
    suspend fun getMe(): UserMeResponse
}
