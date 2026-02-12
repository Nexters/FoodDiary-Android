package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.ClassificationResult
import com.nexters.fooddiary.domain.repository.ClassificationRepository
import javax.inject.Inject

class ClassifyImageUseCase @Inject constructor(
    private val classificationRepository: ClassificationRepository
) {
    suspend operator fun invoke(uriString: String): ClassificationResult? {
        return classificationRepository.classifyImage(uriString)
    }
}
