package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.ClassificationResult

interface ClassificationRepository {
    suspend fun classifyImage(uriString: String): ClassificationResult?
}
