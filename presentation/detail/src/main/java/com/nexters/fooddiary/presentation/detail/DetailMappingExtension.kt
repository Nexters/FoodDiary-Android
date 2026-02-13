package com.nexters.fooddiary.presentation.detail

import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.MealType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun DiaryDetail.toDailyMeals(date: LocalDate): DailyMeals {
    val diaryByMeal = diaries.associateBy { it.mealType }
    return DailyMeals(
        breakfast = MealSlot.BREAKFAST.toMealUiModel(date, diaryByMeal[MealType.BREAKFAST]),
        lunch = MealSlot.LUNCH.toMealUiModel(date, diaryByMeal[MealType.LUNCH]),
        dinner = MealSlot.DINNER.toMealUiModel(date, diaryByMeal[MealType.DINNER]),
    )
}

internal fun MealSlot.toMealUiModel(
    date: LocalDate,
    diary: DiaryEntry?,
): MealCardUiModel {
    if (diary == null) {
        return MealCardUiModel.empty(date, this)
    }

    val firstPhoto = diary.photos.firstOrNull()
    val imageUrls = diary.photos.map { it.imageUrl }
    return MealCardUiModel(
        id = "${date}_${name.lowercase()}",
        date = date,
        slot = this,
        time = firstPhoto?.takenAt?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty(),
        location = diary.location.orEmpty(),
        place = diary.restaurantName.orEmpty(),
        keywords = diary.tags,
        mapLink = diary.mapLink.orEmpty(),
        imageUrls = imageUrls,
        status = if (diary.analysisStatus == AnalysisStatus.PROCESSING) {
            MealCardStatus.PENDING
        } else {
            MealCardStatus.READY
        },
    )
}
