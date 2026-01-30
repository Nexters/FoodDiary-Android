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
        
        val classifiedPhotos = todayPhotos.mapNotNull { mediaItem ->
            val uriString = mediaItem.uri
            val result = classificationRepository.classifyImage(uriString)
            
            if (result != null && result.isFood) {
                ClassifiedPhoto(uriString, result)
            } else {
                null
            }
        }
        
        return classifiedPhotos
            .sortedByDescending { it.result.foodConfidence }
            .map { it.uriString }
    }
    
    private data class ClassifiedPhoto(
        val uriString: String,
        val result: com.nexters.fooddiary.domain.model.ClassificationResult
    )
}
