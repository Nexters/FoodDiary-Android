package com.nexters.fooddiary.data.remote.photo

import com.nexters.fooddiary.data.remote.photo.model.request.ConfirmPhotoRequest
import com.nexters.fooddiary.data.remote.photo.model.request.GetUploadUrlRequest
import com.nexters.fooddiary.data.remote.photo.model.response.ConfirmPhotoResponse
import com.nexters.fooddiary.data.remote.photo.model.response.GetUploadUrlResponse
import com.nexters.fooddiary.data.remote.photo.model.response.PhotoAnalysisResponse
import com.nexters.fooddiary.data.remote.photo.model.response.PhotoFinalRecordResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PhotoApi {
    @POST("/photos")
    suspend fun getUploadUrl(@Body request: GetUploadUrlRequest): GetUploadUrlResponse

    @POST("/photos/{photoId}/analyze")
    suspend fun analyzePhoto(@Path("photoId") photoId: Long): PhotoAnalysisResponse

    @GET("/photos/{photoId}/analysis")
    suspend fun getAnalysisResult(@Path("photoId") photoId: Long): PhotoAnalysisResponse

    @POST("/photos/{photoId}/confirm")
    suspend fun confirmPhoto(
        @Path("photoId") photoId: Long,
        @Body request: ConfirmPhotoRequest
    ): ConfirmPhotoResponse

    @GET("/photos/{photoId}/final")
    suspend fun getFinalRecord(@Path("photoId") photoId: Long): PhotoFinalRecordResponse
}
