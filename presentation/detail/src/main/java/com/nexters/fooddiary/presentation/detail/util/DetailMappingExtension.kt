package com.nexters.fooddiary.presentation.detail.util

import com.nexters.fooddiary.core.common.toLocalTimeText
import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.MealType
import com.nexters.fooddiary.presentation.detail.DailyMeals
import com.nexters.fooddiary.presentation.detail.MealCardStatus
import com.nexters.fooddiary.presentation.detail.MealCardUiModel
import com.nexters.fooddiary.presentation.detail.MealSlot
import java.time.LocalDate

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
        snack = MealSlot.SNACK.toMealUiModel(
            date,
            diaryByMeal[MealType.SNACK]
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
    val mealTimeText = diary.diaryDate.toLocalTimeText()

    return MealCardUiModel(
        id = "${date}_${name.lowercase()}",
        date = date,
        slot = this,
        time = mealTimeText,
        location = diary.location.orEmpty(),
        place = diary.restaurantName.orEmpty(),
        keywords = prefixedTags,
        note = diary.note.orEmpty(),
        mapLink = diary.mapLink.orEmpty(),
        imageUrls = imageUrls,
        status = when (diary.analysisStatus) {
            AnalysisStatus.PROCESSING -> MealCardStatus.PENDING
            AnalysisStatus.FAILED,
            AnalysisStatus.DONE -> MealCardStatus.READY
        },
        diaryId = diary.diaryId.toString(),
    )
}
