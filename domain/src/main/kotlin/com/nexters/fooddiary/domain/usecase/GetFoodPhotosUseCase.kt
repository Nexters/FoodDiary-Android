package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.ClassificationResult
import com.nexters.fooddiary.domain.model.MediaItem
import com.nexters.fooddiary.domain.repository.ClassificationRepository
import com.nexters.fooddiary.domain.repository.MediaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class FoodPhotosResult(
    val foodUris: List<String>,
    val allUris: List<String>
)

class GetFoodPhotosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val classificationRepository: ClassificationRepository
) {
    suspend operator fun invoke(date: LocalDate? = null): FoodPhotosResult = coroutineScope {
        val targetDate = date ?: LocalDate.now()
        val photos = loadPhotosForDate(targetDate)
        if (photos.isEmpty()) {
            return@coroutineScope FoodPhotosResult(foodUris = emptyList(), allUris = emptyList())
        }
        val classified = classifyPhotos(photos)
        FoodPhotosResult(
            foodUris = foodUrisByConfidence(classified),
            allUris = allUrisInOrder(classified)
        )
    }

    private suspend fun loadPhotosForDate(date: LocalDate): List<MediaItem> {
        val photosByMonth = mediaRepository.getPhotosByMonth(YearMonth.from(date))
        return (photosByMonth[date] ?: emptyList())
            .sortedByDescending { it.dateTaken }
    }

    private suspend fun classifyPhotos(photos: List<MediaItem>): List<PhotoWithClassification> =
        coroutineScope {
            photos
                .map { mediaItem ->
                    async {
                        val result = classificationRepository.classifyImage(mediaItem.uri)
                        PhotoWithClassification(mediaItem.uri, result)
                    }
                }
                .awaitAll()
        }

    private fun foodUrisByConfidence(classified: List<PhotoWithClassification>): List<String> =
        classified
            .filter { (_, result) -> result?.isFood == true }
            .sortedByDescending { (_, result) -> result?.foodConfidence ?: 0f }
            .map { it.uriString }

    private fun allUrisInOrder(classified: List<PhotoWithClassification>): List<String> =
        classified.map { it.uriString }

    private data class PhotoWithClassification(
        val uriString: String,
        val result: ClassificationResult?
    )
}
