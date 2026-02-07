package com.nexters.fooddiary.data.remote.diary

import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryRequest
import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryDetailResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DiaryApi {
    @POST("/diaries")
    suspend fun createDiary(@Body request: CreateDiaryRequest): CreateDiaryResponse

    @GET("/diaries/{date}")
    suspend fun getDiary(@Path("date") date: String): DiaryDetailResponse
}
