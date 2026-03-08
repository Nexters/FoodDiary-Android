package com.nexters.fooddiary.data.remote.restaurant

import com.nexters.fooddiary.data.remote.restaurant.model.response.RestaurantSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantApi {
    @GET("/restaurant/search")
    suspend fun searchRestaurant(
        @Query("diary_id") diaryId: Long?,
        @Query("keyword") keyword: String?,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15,
    ): RestaurantSearchResponse
}
