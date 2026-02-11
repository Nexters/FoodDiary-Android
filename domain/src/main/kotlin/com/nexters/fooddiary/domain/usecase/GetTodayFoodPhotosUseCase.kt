package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.ClassificationResult
import com.nexters.fooddiary.domain.repository.ClassificationRepository
import com.nexters.fooddiary.domain.repository.MediaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class GetTodayFoodPhotosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val classificationRepository: ClassificationRepository
) {
    suspend operator fun invoke(): List<String> = coroutineScope {
        val today = LocalDate.now()
        val currentMonth = YearMonth.now()

        val photosByMonth = mediaRepository.getPhotosByMonth(currentMonth)
        val todayPhotos = photosByMonth[today] ?: emptyList()

        if (todayPhotos.isEmpty()) {
            return@coroutineScope emptyList()
        }

        val withResult = todayPhotos
            .map { mediaItem ->
                async {
                    val uriString = mediaItem.uri
                    val result = classificationRepository.classifyImage(uriString)
                    PhotoWithClassification(uriString, result)
                }
            }
            .awaitAll()

        val foodFirst = withResult
            .filter { (_, result) -> result != null && result.isFood }
            .sortedByDescending { (_, result) -> result?.foodConfidence ?: 0f }
            .map { it.uriString }
        val rest = withResult
            .filter { (_, result) -> result == null || !result.isFood }
            .map { it.uriString }

        foodFirst + rest
    }

    private data class PhotoWithClassification(
        val uriString: String,
        val result: ClassificationResult?
    )
}
