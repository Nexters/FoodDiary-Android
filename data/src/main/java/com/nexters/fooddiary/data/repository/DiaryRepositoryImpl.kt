package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.data.remote.diary.model.DiaryAnalysisStatusResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryMealTypeResponse
import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.DiaryPhoto
import com.nexters.fooddiary.domain.model.MealType
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryApi: DiaryApi
) : DiaryRepository {

    override suspend fun getDiary(date: LocalDate): DiaryDetail {
        val requestedDate = date.toString()
        val response = diaryApi.getDiary(
            startDate = requestedDate,
            endDate = requestedDate,
        )
        val diaries = response.diaries.filter { diary ->
            diary.diaryDate == requestedDate
        }

        return DiaryDetail(
            date = date,
            diaries = diaries.map { diary ->
                DiaryEntry(
                    diaryId = diary.diaryId,
                    mealType = diary.timeType.toDomain(),
                    analysisStatus = diary.analysisStatus.toDomain(),
                    createdAt = diary.createdAt,
                    restaurantName = diary.restaurantName,
                    category = diary.category,
                    location = diary.roadAddress,
                    tags = diary.tags,
                    coverPhotoUrl = diary.coverPhotoUrl,
                    mapLink = diary.restaurantUrl,
                    photoCount = diary.photoCount,
                    photos = diary.photos.map { photo ->
                        DiaryPhoto(
                            photoId = photo.photoId,
                            imageUrl = photo.imageUrl,
                        )
                    },
                )
            },
        )
    }

    private fun DiaryMealTypeResponse.toDomain(): MealType {
        return when (this) {
            DiaryMealTypeResponse.BREAKFAST -> MealType.BREAKFAST
            DiaryMealTypeResponse.LUNCH -> MealType.LUNCH
            DiaryMealTypeResponse.DINNER -> MealType.DINNER
        }
    }

    private fun DiaryAnalysisStatusResponse.toDomain(): AnalysisStatus {
        return when (this) {
            DiaryAnalysisStatusResponse.DONE -> AnalysisStatus.DONE
            DiaryAnalysisStatusResponse.PROCESSING -> AnalysisStatus.PROCESSING
        }
    }
}
