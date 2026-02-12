package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryPhotoDetail
import com.nexters.fooddiary.domain.model.MealType
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryApi: DiaryApi
) : DiaryRepository {

    override suspend fun getDiary(date: LocalDate): DiaryDetail {
        val response = diaryApi.getDiary(date.toString())
        return DiaryDetail(
            date = LocalDate.parse(response.date),
            note = response.note,
            photos = response.photos.map { photo ->
                DiaryPhotoDetail(
                    photoId = photo.photoId,
                    imageUrl = photo.imageUrl,
                    takenAt = LocalDateTime.parse(photo.takenAt),
                    location = photo.location,
                    restaurantName = photo.restaurantName,
                    menuName = photo.menuName,
                    menuPrice = photo.menuPrice,
                    mealType = photo.timeType.toMealType(),
                )
            },
        )
    }

    private fun String?.toMealType(): MealType {
        return when (this?.lowercase()) {
            "breakfast" -> MealType.BREAKFAST
            "lunch" -> MealType.LUNCH
            "dinner" -> MealType.DINNER
            else -> MealType.UNKNOWN
        }
    }
}
