package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.ClassificationRepository
import com.nexters.fooddiary.domain.repository.MediaRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class GetTodayFoodPhotosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val classificationRepository: ClassificationRepository
) {
    suspend operator fun invoke(): List<String> {
        val today = LocalDate.now()
        val currentMonth = YearMonth.now()

        val photosByMonth = mediaRepository.getPhotosByMonth(currentMonth)
        val todayPhotos = photosByMonth[today] ?: emptyList()

        if (todayPhotos.isEmpty()) {
            return emptyList()
        }

        val withResult = todayPhotos.map { mediaItem ->
            val uriString = mediaItem.uri
            val result = classificationRepository.classifyImage(uriString)
            PhotoWithClassification(uriString, result)
        }

        val foodFirst = withResult
            .filter { (_, result) -> result != null && result.isFood }
            .sortedByDescending { (_, result) -> result?.foodConfidence ?: 0f }
            .map { it.uriString }
        val rest = withResult
            .filter { (_, result) -> result == null || !result.isFood }
            .map { it.uriString }

        return foodFirst + rest
    }

    private data class PhotoWithClassification(
        val uriString: String,
        val result: com.nexters.fooddiary.domain.model.ClassificationResult?
    )
}
