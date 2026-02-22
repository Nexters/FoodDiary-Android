package com.nexters.fooddiary.data.remote.diary

import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryRequest
import com.nexters.fooddiary.data.remote.diary.model.CreateDiaryResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryDetailResponse
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryByDateItemResponse
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryResponse
import com.nexters.fooddiary.data.remote.diary.model.UpdateDiaryRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryApi {
    @POST("/diaries")
    suspend fun createDiary(@Body request: CreateDiaryRequest): CreateDiaryResponse

    @GET("/diaries")
    suspend fun getDiary(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("test_mode") testMode: Boolean = true,
    ): DiaryDetailResponse

    @GET("/diaries/summary")
    suspend fun getDiarySummary(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("test_mode") testMode: Boolean = true,
    ): Map<String, DiarySummaryByDateItemResponse>

    @GET("/diaries/{id}")
    suspend fun getDiaryById(
        @Path("id") id: Int,
        @Query("test_mode") testMode: Boolean = false
    ): DiarySummaryResponse

    @PATCH("/diaries/{diary_id}")
    suspend fun updateDiary(
        @Path("diary_id") diaryId: Int,
        @Body request: UpdateDiaryRequest,
    ): DiarySummaryResponse

    @DELETE("/diaries/{diary_id}")
    suspend fun deleteDiary(
        @Path("diary_id") diaryId: Int,
    ): Unit
}
