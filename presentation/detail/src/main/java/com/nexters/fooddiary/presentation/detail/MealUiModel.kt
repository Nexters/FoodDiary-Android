package com.nexters.fooddiary.presentation.detail

import java.time.LocalDate

enum class MealSlot {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
}

enum class MealCardStatus {
    EMPTY,
    PENDING,
    READY,
}

data class MealCardUiModel(
    val id: String,
    val date: LocalDate,
    val slot: MealSlot,
    val time: String,
    val location: String,
    val place: String,
    val keywords: List<String>,
    val note: String,
    val mapLink: String,
    val imageUrls: List<String>,
    val status: MealCardStatus,
    val diaryId: String? = null,
) {
    val isEmpty: Boolean get() = status == MealCardStatus.EMPTY
    val isPending: Boolean get() = status == MealCardStatus.PENDING
    val isReady: Boolean get() = status == MealCardStatus.READY

    companion object {
        fun empty(date: LocalDate, slot: MealSlot): MealCardUiModel {
            return MealCardUiModel(
                id = "${date}_${slot.name.lowercase()}",
                date = date,
                slot = slot,
                time = "",
                location = "",
                place = "",
                keywords = emptyList(),
                note = "",
                mapLink = "",
                imageUrls = emptyList(),
                status = MealCardStatus.EMPTY,
            )
        }
    }
}

data class DailyMeals(
    val breakfast: MealCardUiModel,
    val lunch: MealCardUiModel,
    val dinner: MealCardUiModel,
    val snack: MealCardUiModel,
) {
    fun asOrderedList(): List<MealCardUiModel> {
        return listOf(breakfast, lunch, dinner, snack)
    }

    companion object {
        fun empty(date: LocalDate): DailyMeals {
            return DailyMeals(
                breakfast = MealCardUiModel.empty(date, MealSlot.BREAKFAST),
                lunch = MealCardUiModel.empty(date, MealSlot.LUNCH),
                dinner = MealCardUiModel.empty(date, MealSlot.DINNER),
                snack = MealCardUiModel.empty(date, MealSlot.SNACK),
            )
        }
    }
}
