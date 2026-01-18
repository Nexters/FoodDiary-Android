package com.nexters.fooddiary.core.classification

import com.nexters.fooddiary.core.common.toPercentageString

private const val CONFIDENCE_RANGE_START = 0f
private const val CONFIDENCE_RANGE_END = 1f

data class FoodClassificationResult(
    val isFood: Boolean,
    val foodConfidence: Float,
    val notFoodConfidence: Float
) {
    init {
        require(foodConfidence in CONFIDENCE_RANGE_START..CONFIDENCE_RANGE_END) { 
            "foodConfidence must be in range [$CONFIDENCE_RANGE_START, $CONFIDENCE_RANGE_END], but was $foodConfidence" 
        }
        require(notFoodConfidence in CONFIDENCE_RANGE_START..CONFIDENCE_RANGE_END) { 
            "notFoodConfidence must be in range [$CONFIDENCE_RANGE_START, $CONFIDENCE_RANGE_END], but was $notFoodConfidence" 
        }
    }

    val maxConfidence: Float
        get() = maxOf(foodConfidence, notFoodConfidence)

    private fun formatConfidence(decimalPlaces: Int = 1): String =
        when {
            isFood -> foodConfidence
            else -> notFoodConfidence
        }.toPercentageString(decimalPlaces)

    fun toDisplayMessage(
        foodMessage: String,
        notFoodMessage: String
    ): String {
        val confidence = formatConfidence()
        return when {
            isFood -> foodMessage.format(confidence)
            else -> notFoodMessage.format(confidence)
        }
    }

    companion object {
        private const val UINT8_MAX = 255f
        private const val EXPECTED_OUTPUT_SIZE = 2

        fun fromModelOutput(output: ByteArray): FoodClassificationResult {
            require(output.size == EXPECTED_OUTPUT_SIZE) { 
                "Output array size must be $EXPECTED_OUTPUT_SIZE, but was ${output.size}" 
            }

            val foodRaw = output[0].toUByte().toInt()
            val notFoodRaw = output[1].toUByte().toInt()

            val foodConfidence = foodRaw.div(UINT8_MAX)
            val notFoodConfidence = notFoodRaw.div(UINT8_MAX)

            return FoodClassificationResult(
                isFood = foodRaw > notFoodRaw,
                foodConfidence = foodConfidence,
                notFoodConfidence = notFoodConfidence
            )
        }
    }
}
