package com.nexters.fooddiary.domain.model

data class UserInsights(
    val month: String,
    val photoStats: InsightPhotoStats,
    val categoryStats: InsightCategoryStats,
    val weeklyStats: InsightWeeklyStats,
    val diaryTimeStats: InsightDiaryTimeStats,
    val tagStats: List<InsightTagStat>,
    val locationStats: List<InsightLocationStat>,
)

data class InsightPhotoStats(
    val currentMonthCount: Int,
    val previousMonthCount: Int,
    val changeRate: Double,
)

data class InsightCategoryStats(
    val currentMonth: InsightCategoryInfo,
    val previousMonth: InsightCategoryInfo,
    val currentMonthCounts: InsightCategoryCounts,
)

data class InsightCategoryInfo(
    val topCategory: String,
    val count: Int,
)

data class InsightCategoryCounts(
    val korean: Int,
    val chinese: Int,
    val japanese: Int,
    val western: Int,
    val etc: Int,
    val homeCooked: Int,
)

data class InsightWeeklyStats(
    val mostActiveWeek: Int,
    val weeklyCounts: List<InsightWeekStat>,
)

data class InsightWeekStat(
    val week: Int,
    val count: Int,
)

data class InsightDiaryTimeStats(
    val mostActiveTime: String,
    val distribution: List<InsightTimeSlotStat>,
)

data class InsightTimeSlotStat(
    val time: String,
    val count: Int,
)

data class InsightTagStat(
    val keyword: String,
    val count: Int,
)

data class InsightLocationStat(
    val dong: String,
    val count: Int,
)
