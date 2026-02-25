package com.nexters.fooddiary.data.mapper

import com.nexters.fooddiary.data.remote.diary.model.DiaryPhoto
import com.nexters.fooddiary.data.remote.diary.model.DiarySummaryResponse
import com.nexters.fooddiary.domain.model.AnalysisStatus
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.DiaryPhoto as DomainDiaryPhoto
import com.nexters.fooddiary.domain.model.MealType
import javax.inject.Inject

class DiaryMapper @Inject constructor() {
    fun toDomainDiaryEntries(diaries: List<DiarySummaryResponse>): List<DiaryEntry> {
        return diaries.mapNotNull { diary ->
            val mealType = diary.timeType.toDomainOrNull() ?: return@mapNotNull null
            DiaryEntry(
                diaryId = diary.diaryId,
                mealType = mealType,
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

    private fun String?.toDomainOrNull(): MealType? {
        return when (this?.trim()?.lowercase()) {
            "breakfast" -> MealType.BREAKFAST
            "lunch" -> MealType.LUNCH
            "dinner" -> MealType.DINNER
            else -> null
        }
    }

    private fun String?.toDomain(): AnalysisStatus {
        return when (this?.trim()?.lowercase()) {
            "done" -> AnalysisStatus.DONE
            "processing" -> AnalysisStatus.PROCESSING
            else -> AnalysisStatus.PROCESSING
        }
    }
}
