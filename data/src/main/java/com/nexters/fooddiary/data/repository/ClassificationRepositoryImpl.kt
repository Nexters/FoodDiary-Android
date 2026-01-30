package com.nexters.fooddiary.data.repository

import android.content.Context
import android.net.Uri
import com.nexters.fooddiary.core.classification.FoodClassificationResult
import com.nexters.fooddiary.core.classification.FoodClassifier
import com.nexters.fooddiary.core.classification.ImageUtils
import com.nexters.fooddiary.domain.model.ClassificationResult
import com.nexters.fooddiary.domain.repository.ClassificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassificationRepositoryImpl @Inject constructor(
    private val foodClassifier: FoodClassifier,
    @ApplicationContext private val context: Context
) : ClassificationRepository {

    private val classificationCache = mutableMapOf<String, FoodClassificationResult>()

    override suspend fun classifyImage(uriString: String): ClassificationResult? {
        return withContext(Dispatchers.IO) {
            val cacheKey = normalizeCacheKey(uriString)
            val cachedResult = classificationCache[cacheKey]

            if (cachedResult != null) {
                return@withContext cachedResult.toDomainModel()
            }
            val uri = Uri.parse(uriString)
            val bitmap = ImageUtils.uriToBitmap(context, uri) ?: return@withContext null

            try {
                val result = foodClassifier.classifyAsync(bitmap)
                classificationCache[cacheKey] = result
                result.toDomainModel()
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun normalizeCacheKey(uriString: String): String {
        return try {
            Uri.parse(uriString).toString()
        } catch (e: Exception) {
            uriString
        }
    }

    fun clearCache() {
        classificationCache.clear()
    }

    private fun FoodClassificationResult.toDomainModel(): ClassificationResult {
        return ClassificationResult(
            isFood = this.isFood,
            foodConfidence = this.foodConfidence,
            notFoodConfidence = this.notFoodConfidence
        )
    }
}
