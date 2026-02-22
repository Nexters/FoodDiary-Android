package com.nexters.fooddiary.presentation.detail.util

import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.MealType
import com.nexters.fooddiary.presentation.detail.DailyMeals
import com.nexters.fooddiary.presentation.detail.MealCardStatus
import com.nexters.fooddiary.presentation.detail.MealCardUiModel
import com.nexters.fooddiary.presentation.detail.MealSlot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun DiaryDetail.toDailyMeals(date: LocalDate): DailyMeals {
    val diaryByMeal = diaries.associateBy { it.mealType }
    return DailyMeals(
        breakfast = MealSlot.BREAKFAST.toMealUiModel(
            date,
            diaryByMeal[MealType.BREAKFAST]
        ),
        lunch = MealSlot.LUNCH.toMealUiModel(
            date,
            diaryByMeal[MealType.LUNCH]
        ),
        dinner = MealSlot.DINNER.toMealUiModel(
            date,
            diaryByMeal[MealType.DINNER]
        ),
    )
}

internal fun MealSlot.toMealUiModel(
    date: LocalDate,
    diary: DiaryEntry?,
): MealCardUiModel {
    if (diary == null) {
        return MealCardUiModel.empty(date, this)
    }

    val imageUrls = diary.photos.map { it.imageUrl }
    val prefixedTags = diary.tags
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { tag -> if (tag.startsWith("#")) tag else "#$tag" }
    val mealTimeText = if (diary.createdAt.isNullOrEmpty()) {
        ""
    } else {
        runCatching { LocalDateTime.parse(diary.createdAt) }
            .getOrNull()
            ?.format(DateTimeFormatter.ofPattern("HH:mm"))
            .orEmpty()
    }

    return MealCardUiModel(
        id = "${date}_${name.lowercase()}",
        diaryId = diary.diaryId,
        date = date,
        slot = this,
        time = mealTimeText,
        location = diary.location.orEmpty(),
        place = diary.restaurantName.orEmpty(),
        keywords = prefixedTags,
        mapLink = diary.mapLink.orEmpty(),
        imageUrls = imageUrls,
        status = if (diary.analysisStatus == AnalysisStatus.PROCESSING) {
            MealCardStatus.PENDING
        } else {
            MealCardStatus.READY
        },
    )
}
