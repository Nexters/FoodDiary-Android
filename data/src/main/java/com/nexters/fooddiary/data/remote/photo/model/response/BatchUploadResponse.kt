package com.nexters.fooddiary.data.remote.photo.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchUploadResponse(
    @SerialName("results")
    val results: List<BatchUploadResultItem>
)

@Serializable
data class BatchUploadResultItem(
    @SerialName("analysis")
    val analysis: BatchUploadAnalysis?,
    @SerialName("diary_id")
    val diaryId: Long,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("photo_id")
    val photoId: Long,
    @SerialName("time_type")
    val timeType: String
)

@Serializable
data class BatchUploadAnalysis(
    @SerialName("food_category")
    val foodCategory: String? = null,
    @SerialName("keywords")
    val keywords: List<String>? = null,
    @SerialName("menu_candidates")
    val menuCandidates: List<BatchUploadMenuCandidate>? = null,
    @SerialName("restaurant_candidates")
    val restaurantCandidates: List<BatchUploadRestaurantCandidate>? = null
)

@Serializable
data class BatchUploadMenuCandidate(
    @SerialName("name")
    val name: String
)

@Serializable
data class BatchUploadRestaurantCandidate(
    @SerialName("confidence")
    val confidence: Double,
    @SerialName("name")
    val name: String
)
