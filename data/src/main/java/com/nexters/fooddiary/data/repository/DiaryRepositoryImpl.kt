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
        val responseByDate = diaryApi.getDiary(date.toString())
        val dayResponse = responseByDate[date.toString()] ?: responseByDate.values.firstOrNull()
            ?: return DiaryDetail(
                date = date,
                note = "",
                photos = emptyList(),
            )

        return DiaryDetail(
            date = date,
            note = "",
            photos = dayResponse.diaries.flatMap { diary ->
                val mealType = diary.timeType.toMealType()
                val isProcessing = diary.analysisStatus.equals("processing", ignoreCase = true)
                diary.photos.map { photo ->
                    DiaryPhotoDetail(
                        photoId = photo.photoId,
                        imageUrl = photo.imageUrl,
                        takenAt = photo.takenAt?.let(LocalDateTime::parse),
                        location = null,
                        restaurantName = diary.restaurantName,
                        menuName = null,
                        menuPrice = null,
                        mapLink = diary.mapLink,
                        isProcessing = isProcessing,
                        mealType = mealType,
                    )
                }
            }
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
