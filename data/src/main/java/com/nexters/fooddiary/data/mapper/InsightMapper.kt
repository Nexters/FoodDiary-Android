package com.nexters.fooddiary.data.mapper

import com.nexters.fooddiary.data.remote.insight.model.response.CategoryCountsResponse
import com.nexters.fooddiary.data.remote.insight.model.response.CategoryInfoResponse
import com.nexters.fooddiary.data.remote.insight.model.response.CategoryStatsResponse
import com.nexters.fooddiary.data.remote.insight.model.response.DiaryTimeStatsResponse
import com.nexters.fooddiary.data.remote.insight.model.response.InsightsResponse
import com.nexters.fooddiary.data.remote.insight.model.response.LocationStatResponse
import com.nexters.fooddiary.data.remote.insight.model.response.PhotoStatsResponse
import com.nexters.fooddiary.data.remote.insight.model.response.TagStatResponse
import com.nexters.fooddiary.data.remote.insight.model.response.TimeSlotDistributionResponse
import com.nexters.fooddiary.data.remote.insight.model.response.WeekStatResponse
import com.nexters.fooddiary.data.remote.insight.model.response.WeeklyStatsResponse
import com.nexters.fooddiary.domain.model.InsightCategoryCounts
import com.nexters.fooddiary.domain.model.InsightCategoryInfo
import com.nexters.fooddiary.domain.model.InsightCategoryStats
import com.nexters.fooddiary.domain.model.InsightDiaryTimeStats
import com.nexters.fooddiary.domain.model.InsightLocationStat
import com.nexters.fooddiary.domain.model.InsightPhotoStats
import com.nexters.fooddiary.domain.model.InsightTagStat
import com.nexters.fooddiary.domain.model.InsightTimeSlotStat
import com.nexters.fooddiary.domain.model.InsightWeekStat
import com.nexters.fooddiary.domain.model.InsightWeeklyStats
import com.nexters.fooddiary.domain.model.UserInsights

internal fun InsightsResponse.toDomain(): UserInsights {
    return UserInsights(
        month = month,
        photoStats = photoStats.toDomain(),
        categoryStats = categoryStats.toDomain(),
        weeklyStats = weeklyStats.toDomain(),
        diaryTimeStats = diaryTimeStats.toDomain(),
        tagStats = tagStats.map { it.toDomain() },
        locationStats = locationStats.map { it.toDomain() },
    )
}

private fun PhotoStatsResponse.toDomain(): InsightPhotoStats {
    return InsightPhotoStats(
        currentMonthCount = currentMonthCount,
        previousMonthCount = previousMonthCount,
        changeRate = changeRate,
    )
}

private fun CategoryStatsResponse.toDomain(): InsightCategoryStats {
    return InsightCategoryStats(
        currentMonth = currentMonth.toDomain(),
        previousMonth = previousMonth.toDomain(),
        currentMonthCounts = currentMonthCounts.toDomain(),
    )
}

private fun CategoryInfoResponse.toDomain(): InsightCategoryInfo {
    return InsightCategoryInfo(
        topCategory = topCategory,
        count = count,
    )
}

private fun CategoryCountsResponse.toDomain(): InsightCategoryCounts {
    return InsightCategoryCounts(
        korean = korean,
        chinese = chinese,
        japanese = japanese,
        western = western,
        etc = etc,
        homeCooked = homeCooked,
    )
}

private fun WeeklyStatsResponse.toDomain(): InsightWeeklyStats {
    return InsightWeeklyStats(
        mostActiveWeek = mostActiveWeek,
        weeklyCounts = weeklyCounts.map { it.toDomain() },
    )
}

private fun WeekStatResponse.toDomain(): InsightWeekStat {
    return InsightWeekStat(
        week = week,
        count = count,
    )
}

private fun DiaryTimeStatsResponse.toDomain(): InsightDiaryTimeStats {
    return InsightDiaryTimeStats(
        mostActiveTime = mostActiveTime,
        distribution = distribution.map { it.toDomain() },
    )
}

private fun TimeSlotDistributionResponse.toDomain(): InsightTimeSlotStat {
    return InsightTimeSlotStat(
        time = time,
        count = count,
    )
}

private fun TagStatResponse.toDomain(): InsightTagStat {
    return InsightTagStat(
        keyword = keyword,
        count = count,
    )
}

private fun LocationStatResponse.toDomain(): InsightLocationStat {
    return InsightLocationStat(
        dong = dong,
        count = count,
    )
}
