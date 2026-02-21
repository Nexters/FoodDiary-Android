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
import java.time.LocalDateTime
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
            diary.diaryDate == null || diary.diaryDate == requestedDate
        }

        return DiaryDetail(
            date = date,
            diaries = diaries.map { diary ->
                DiaryEntry(
                    diaryId = diary.diaryId,
                    mealType = diary.timeType.toDomainOrThrow(diary.diaryId),
                    analysisStatus = diary.analysisStatus.toDomainOrThrow(diary.diaryId),
                    restaurantName = diary.restaurantName,
                    category = diary.category,
                    location = diary.roadAddress,
                    tags = diary.tags,
                    coverPhotoUrl = diary.coverPhotoUrl,
                    mapLink = diary.restaurantUrl,
                    photoCount = diary.photoCount ?: diary.photos.size,
                    photos = diary.photos.map { photo ->
                        DiaryPhoto(
                            photoId = photo.photoId,
                            imageUrl = photo.imageUrl,
                            takenAt = photo.takenAt?.let { timestamp ->
                                runCatching { LocalDateTime.parse(timestamp) }.getOrNull()
                            },
                        )
                    },
                )
            },
        )
    }

    private fun DiaryMealTypeResponse?.toDomainOrThrow(diaryId: Long): MealType {
        return when (this) {
            DiaryMealTypeResponse.BREAKFAST -> MealType.BREAKFAST
            DiaryMealTypeResponse.LUNCH -> MealType.LUNCH
            DiaryMealTypeResponse.DINNER -> MealType.DINNER
            null -> error("Invalid time_type for diaryId=$diaryId")
        }
    }

    private fun DiaryAnalysisStatusResponse?.toDomainOrThrow(diaryId: Long): AnalysisStatus {
        return when (this) {
            DiaryAnalysisStatusResponse.DONE -> AnalysisStatus.DONE
            DiaryAnalysisStatusResponse.PROCESSING -> AnalysisStatus.PROCESSING
            null -> error("Invalid analysis_status for diaryId=$diaryId")
        }
    }
}
