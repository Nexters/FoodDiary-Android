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
                    mealType = diary.timeType.toDomainOrThrow(diary.diaryId),
                    analysisStatus = diary.analysisStatus.toDomainOrThrow(diary.diaryId),
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
