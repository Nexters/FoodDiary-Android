package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.DiaryPhoto
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
        val dayResponse = responseByDate[date.toString()]
            ?: return DiaryDetail(
                date = date,
                diaries = emptyList(),
            )

        return DiaryDetail(
            date = date,
            diaries = dayResponse.diaries.map { diary ->
                DiaryEntry(
                    diaryId = diary.diaryId,
                    mealType = diary.timeType.toMealType(),
                    analysisStatus = diary.analysisStatus.toAnalysisStatus(),
                    restaurantName = diary.restaurantName,
                    category = diary.category,
                    location = diary.location,
                    tags = diary.tags,
                    coverPhotoUrl = diary.coverPhotoUrl,
                    mapLink = diary.mapLink,
                    photoCount = diary.photoCount ?: diary.photos.size,
                    photos = diary.photos.map { photo ->
                        DiaryPhoto(
                            photoId = photo.photoId,
                            imageUrl = photo.imageUrl,
                            takenAt = photo.takenAt?.let(LocalDateTime::parse),
                        )
                    },
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

    private fun String?.toAnalysisStatus(): AnalysisStatus {
        return when (this?.lowercase()) {
            "done" -> AnalysisStatus.DONE
            "processing" -> AnalysisStatus.PROCESSING
            else -> AnalysisStatus.UNKNOWN
        }
    }
}
