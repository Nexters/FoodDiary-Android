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

data class TodayPhotosResult(
    val foodUris: List<String>,
    val allUris: List<String>
)

class GetTodayFoodPhotosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val classificationRepository: ClassificationRepository
) {
    suspend operator fun invoke(): TodayPhotosResult = coroutineScope {
        val todayPhotos = loadTodayPhotos()
        if (todayPhotos.isEmpty()) {
            return@coroutineScope TodayPhotosResult(foodUris = emptyList(), allUris = emptyList())
        }
        val classified = classifyPhotos(todayPhotos)
        TodayPhotosResult(
            foodUris = foodUrisByConfidence(classified),
            allUris = allUrisInOrder(classified)
        )
    }

    private suspend fun loadTodayPhotos(): List<MediaItem> {
        val photosByMonth = mediaRepository.getPhotosByMonth(YearMonth.now())
        return photosByMonth[LocalDate.now()] ?: emptyList()
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
