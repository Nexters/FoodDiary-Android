package com.nexters.fooddiary.data.remote.insight.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsightsResponse(
    @SerialName("month")
    val month: String,
    @SerialName("photo_stats")
    val photoStats: PhotoStatsResponse,
    @SerialName("category_stats")
    val categoryStats: CategoryStatsResponse,
    @SerialName("weekly_stats")
    val weeklyStats: WeeklyStatsResponse,
    @SerialName("diary_time_stats")
    val diaryTimeStats: DiaryTimeStatsResponse,
    @SerialName("tag_stats")
    val tagStats: List<TagStatResponse> = emptyList(),
    @SerialName("location_stats")
    val locationStats: List<LocationStatResponse> = emptyList(),
)

@Serializable
data class PhotoStatsResponse(
    @SerialName("current_month_count")
    val currentMonthCount: Int,
    @SerialName("previous_month_count")
    val previousMonthCount: Int,
    @SerialName("change_rate")
    val changeRate: Double,
)

@Serializable
data class CategoryStatsResponse(
    @SerialName("current_month")
    val currentMonth: CategoryInfoResponse,
    @SerialName("previous_month")
    val previousMonth: CategoryInfoResponse,
    @SerialName("current_month_counts")
    val currentMonthCounts: CategoryCountsResponse,
)

@Serializable
data class CategoryInfoResponse(
    @SerialName("top_category")
    val topCategory: String,
    @SerialName("count")
    val count: Int,
)

@Serializable
data class CategoryCountsResponse(
    @SerialName("korean")
    val korean: Int = 0,
    @SerialName("chinese")
    val chinese: Int = 0,
    @SerialName("japanese")
    val japanese: Int = 0,
    @SerialName("western")
    val western: Int = 0,
    @SerialName("etc")
    val etc: Int = 0,
    @SerialName("home_cooked")
    val homeCooked: Int = 0,
)

@Serializable
data class WeeklyStatsResponse(
    @SerialName("most_active_week")
    val mostActiveWeek: Int,
    @SerialName("weekly_counts")
    val weeklyCounts: List<WeekStatResponse> = emptyList(),
)

@Serializable
data class WeekStatResponse(
    @SerialName("week")
    val week: Int,
    @SerialName("count")
    val count: Int,
)

@Serializable
data class DiaryTimeStatsResponse(
    @SerialName("most_active_time")
    val mostActiveTime: String,
    @SerialName("distribution")
    val distribution: List<TimeSlotDistributionResponse> = emptyList(),
)

@Serializable
data class TimeSlotDistributionResponse(
    @SerialName("time")
    val time: String,
    @SerialName("count")
    val count: Int,
)

@Serializable
data class TagStatResponse(
    @SerialName("keyword")
    val keyword: String,
    @SerialName("count")
    val count: Int,
)

@Serializable
data class LocationStatResponse(
    @SerialName("dong")
    val dong: String,
    @SerialName("count")
    val count: Int,
)
