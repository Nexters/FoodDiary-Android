package com.nexters.fooddiary.presentation.detail.util

import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.MealType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun DiaryDetail.toDailyMeals(date: LocalDate): com.nexters.fooddiary.presentation.detail.DailyMeals {
    val diaryByMeal = diaries.associateBy { it.mealType }
    return _root_ide_package_.com.nexters.fooddiary.presentation.detail.DailyMeals(
        breakfast = _root_ide_package_.com.nexters.fooddiary.presentation.detail.MealSlot.BREAKFAST.toMealUiModel(
            date,
            diaryByMeal[MealType.BREAKFAST]
        ),
        lunch = _root_ide_package_.com.nexters.fooddiary.presentation.detail.MealSlot.LUNCH.toMealUiModel(
            date,
            diaryByMeal[MealType.LUNCH]
        ),
        dinner = _root_ide_package_.com.nexters.fooddiary.presentation.detail.MealSlot.DINNER.toMealUiModel(
            date,
            diaryByMeal[MealType.DINNER]
        ),
    )
}

internal fun com.nexters.fooddiary.presentation.detail.MealSlot.toMealUiModel(
    date: LocalDate,
    diary: DiaryEntry?,
): com.nexters.fooddiary.presentation.detail.MealCardUiModel {
    if (diary == null) {
        return _root_ide_package_.com.nexters.fooddiary.presentation.detail.MealCardUiModel.Companion.empty(date, this)
    }

    val firstPhoto = diary.photos.firstOrNull()
    val imageUrls = diary.photos.map { it.imageUrl }
    val prefixedTags = diary.tags
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { tag -> if (tag.startsWith("#")) tag else "#$tag" }

    return _root_ide_package_.com.nexters.fooddiary.presentation.detail.MealCardUiModel(
        id = "${date}_${name.lowercase()}",
        date = date,
        slot = this,
        time = firstPhoto?.takenAt?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty(),
        location = diary.location.orEmpty(),
        place = diary.restaurantName.orEmpty(),
        keywords = prefixedTags,
        mapLink = diary.mapLink.orEmpty(),
        imageUrls = imageUrls,
        status = if (diary.analysisStatus == AnalysisStatus.PROCESSING) {
            _root_ide_package_.com.nexters.fooddiary.presentation.detail.MealCardStatus.PENDING
        } else {
            _root_ide_package_.com.nexters.fooddiary.presentation.detail.MealCardStatus.READY
        },
    )
}
