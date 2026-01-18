package com.nexters.fooddiary.presentation.image

import android.graphics.Bitmap
import com.airbnb.mvrx.MavericksState
import com.nexters.fooddiary.core.classification.FoodClassificationResult

internal data class ImageClassificationState(
    val selectedImage: Bitmap? = null,
    val classificationResult: ClassificationResult? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : MavericksState {
    val hasSelectedImage: Boolean
        get() = selectedImage != null

    val isClassifying: Boolean
        get() = isLoading && selectedImage != null
}

internal sealed class ClassificationResult {
    data class Complete(val result: FoodClassificationResult) : ClassificationResult()
}

