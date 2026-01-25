package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.nexters.fooddiary.core.classification.FoodClassifier
import com.nexters.fooddiary.core.classification.ImageUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

internal class ImageClassificationViewModel @AssistedInject constructor(
    @Assisted initialState: ImageClassificationState,
    @ApplicationContext private val context: Context
) : MavericksViewModel<ImageClassificationState>(initialState) {
    @Inject lateinit var classifier: FoodClassifier

    fun loadImageFromUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            updateLoadingState(isLoading = true)
            
            val bitmap = loadBitmapFromUri(uri)
            
            withContext(Dispatchers.Main) {
                when {
                    bitmap != null -> handleImageLoaded(bitmap)
                    else -> handleImageLoadError()
                }
            }
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return ImageUtils.uriToBitmap(context, uri)
    }

    private fun handleImageLoaded(bitmap: Bitmap) {
        updateStateWithImage(bitmap)
        startClassificationIfPossible(bitmap)
    }

    private fun updateStateWithImage(bitmap: Bitmap) {
        setState {
            copy(
                selectedImage = bitmap,
                isLoading = false,
                errorMessage = null,
                classificationResult = null
            )
        }
    }

    private fun startClassificationIfPossible(bitmap: Bitmap) {
        when {
            classifier != null -> classifyImage(bitmap, classifier!!)
            else -> handleClassifierNotAvailableError()
        }
    }

    private fun handleClassifierNotAvailableError() {
        setState {
            copy(
                errorMessage = context.getString(R.string.image_model_load_failed),
                isLoading = false
            )
        }
    }

    private fun handleImageLoadError() {
        setState {
            copy(
                errorMessage = context.getString(R.string.image_load_failed),
                isLoading = false
            )
        }
    }

    private fun classifyImage(bitmap: Bitmap, classifier: FoodClassifier) {
        viewModelScope.launch(Dispatchers.Default) {
            updateLoadingState(isLoading = true)
            
            try {
                val result = classifier.classifyAsync(bitmap)
                handleClassificationSuccess(result)
            } catch (e: Exception) {
                handleClassificationError(e)
            }
        }
    }

    private fun handleClassificationSuccess(result: com.nexters.fooddiary.core.classification.FoodClassificationResult) {
        setState {
            copy(
                classificationResult = ClassificationResult.Complete(result),
                isLoading = false
            )
        }
    }

    private fun handleClassificationError(exception: Exception) {
        val errorMessage = createClassificationErrorMessage(exception)
        setState {
            copy(
                errorMessage = errorMessage,
                isLoading = false
            )
        }
    }

    private fun createClassificationErrorMessage(exception: Exception): String {
        val fallbackMessage = context.getString(R.string.image_load_failed)
        val exceptionMessage = exception.message ?: fallbackMessage
        return context.getString(
            R.string.image_classification_error,
            exceptionMessage
        )
    }

    private fun updateLoadingState(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    fun clearImage() {
        setState {
            copy(
                selectedImage = null,
                classificationResult = null,
                errorMessage = null,
                isLoading = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        classifier?.close()
    }

    @AssistedFactory
    interface Factory {
        fun create(state: ImageClassificationState): ImageClassificationViewModel
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ViewModelFactoryEntryPoint {
        fun factory(): Factory
    }

    companion object : MavericksViewModelFactory<ImageClassificationViewModel, ImageClassificationState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: ImageClassificationState
        ): ImageClassificationViewModel {
            val entryPoint = EntryPointAccessors.fromApplication(
                viewModelContext.app(),
                ViewModelFactoryEntryPoint::class.java
            )
            return entryPoint.factory().create(state)
        }
    }
}
