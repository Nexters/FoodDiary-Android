package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.net.Uri
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.classification.ImageUtils
import com.nexters.fooddiary.domain.model.ClassificationResult
import com.nexters.fooddiary.domain.usecase.ClassifyImageUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageClassificationViewModel @AssistedInject constructor(
    @Assisted initialState: ImageClassificationState,
    @ApplicationContext private val context: Context,
    private val classifyImageUseCase: ClassifyImageUseCase
) : MavericksViewModel<ImageClassificationState>(initialState) {

    fun loadImagesFromUris(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isLoading = true, errorMessage = null) }
            val loaded = uris.mapNotNull { uri ->
                ImageUtils.uriToBitmap(context, uri)?.let { bitmap -> uri to bitmap }
            }
            if (loaded.isEmpty()) {
                setState {
                    copy(
                        selectedItems = emptyList(),
                        isLoading = false,
                        errorMessage = context.getString(R.string.image_load_failed)
                    )
                }
                return@launch
            }
            val items = loaded.map { (_, bitmap) -> ClassifiedImageItem(bitmap, null) }
            withContext(Dispatchers.Main) {
                setState { copy(selectedItems = items, isLoading = true) }
            }
            loaded.forEachIndexed { index, (uri, _) ->
                val result = runCatching {
                    classifyImageUseCase(uri.toString())
                }.getOrNull()
                val idx = index
                withContext(Dispatchers.Main) {
                    setState {
                        copy(
                            selectedItems = selectedItems.mapIndexed { i, item ->
                                if (i == idx) item.copy(classificationResult = result)
                                else item
                            },
                            isLoading = idx < loaded.lastIndex
                        )
                    }
                }
            }
        }
    }

    fun clearImage() {
        setState {
            copy(
                selectedItems = emptyList(),
                errorMessage = null,
                isLoading = false
            )
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ImageClassificationViewModel, ImageClassificationState> {
        override fun create(state: ImageClassificationState): ImageClassificationViewModel
    }

    companion object : MavericksViewModelFactory<ImageClassificationViewModel, ImageClassificationState> by hiltMavericksViewModelFactory()
}
