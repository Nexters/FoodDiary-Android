package com.nexters.fooddiary.domain.model

data class ClassificationResult(
    val isFood: Boolean,
    val foodConfidence: Float,
    val notFoodConfidence: Float
) {
    val maxConfidence: Float
        get() = maxOf(foodConfidence, notFoodConfidence)
}
