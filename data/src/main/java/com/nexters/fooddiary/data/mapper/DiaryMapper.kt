package com.nexters.fooddiary.data.mapper

import com.nexters.fooddiary.data.remote.diary.model.DiaryAnalysisStatusResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryMealTypeResponse
import com.nexters.fooddiary.data.remote.diary.model.DiaryPhoto
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryResponse
import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.DiaryPhoto as DomainDiaryPhoto
import com.nexters.fooddiary.domain.model.MealType
import javax.inject.Inject

class DiaryMapper @Inject constructor() {
    fun toDomainDiaryEntries(diaries: List<DiarySummaryResponse>): List<DiaryEntry> {
        return diaries.map { diary ->
            DiaryEntry(
                diaryId = diary.diaryId,
                mealType = diary.timeType.toDomain(),
                analysisStatus = diary.analysisStatus.toDomain(),
                createdAt = diary.createdAt,
                restaurantName = diary.restaurantName,
                category = diary.category,
                location = diary.roadAddress,
                tags = diary.tags,
                note = diary.note,
                coverPhotoUrl = diary.coverPhotoUrl,
                coverPhotoId = diary.coverPhotoId,
                mapLink = diary.restaurantUrl,
                photoCount = diary.photoCount,
                photos = diary.photos.map { photo ->
                    photo.toDomain()
                },
            )
        }
    }

    private fun DiaryPhoto.toDomain(): DomainDiaryPhoto {
        return DomainDiaryPhoto(
            photoId = photoId,
            imageUrl = imageUrl,
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
