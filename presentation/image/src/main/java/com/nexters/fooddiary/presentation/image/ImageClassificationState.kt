package com.nexters.fooddiary.presentation.image

import android.graphics.Bitmap
import com.airbnb.mvrx.MavericksState
import com.nexters.fooddiary.domain.model.ClassificationResult

data class ClassifiedImageItem(
    val bitmap: Bitmap,
    val classificationResult: ClassificationResult? = null
)

data class ImageClassificationState(
    val selectedItems: List<ClassifiedImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : MavericksState {
    val hasSelectedImage: Boolean
        get() = selectedItems.isNotEmpty()

    val isClassifying: Boolean
        get() = isLoading && selectedItems.isNotEmpty()
}
