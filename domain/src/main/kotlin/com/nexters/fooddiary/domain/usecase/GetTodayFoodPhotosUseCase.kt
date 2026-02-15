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

data class TodayPhotosResult(
    val foodUris: List<String>,
    val allUris: List<String>
)

class GetTodayFoodPhotosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val classificationRepository: ClassificationRepository
) {
    suspend operator fun invoke(): TodayPhotosResult = coroutineScope {
        val today = LocalDate.now()
        val currentMonth = YearMonth.now()

        val photosByMonth = mediaRepository.getPhotosByMonth(currentMonth)
        val todayPhotos = photosByMonth[today] ?: emptyList()

        if (todayPhotos.isEmpty()) {
            return@coroutineScope TodayPhotosResult(foodUris = emptyList(), allUris = emptyList())
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

        val allUris = withResult.map { it.uriString }
        val foodUris = withResult
            .filter { (_, result) -> result != null && result.isFood }
            .sortedByDescending { (_, result) -> result?.foodConfidence ?: 0f }
            .map { it.uriString }

        TodayPhotosResult(foodUris = foodUris, allUris = allUris)
    }

    private data class PhotoWithClassification(
        val uriString: String,
        val result: ClassificationResult?
    )
}
