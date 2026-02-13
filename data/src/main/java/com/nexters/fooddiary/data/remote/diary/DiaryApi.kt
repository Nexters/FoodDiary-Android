package com.nexters.fooddiary.data.remote.diary

import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryRequest
import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryDetailResponseByDate
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DiaryApi {
    @POST("/diaries")
    suspend fun createDiary(@Body request: CreateDiaryRequest): CreateDiaryResponse

    @GET("/diaries")
    suspend fun getDiary(@Query("date") date: String): Map<String, DiaryDetailResponseByDate>
}
