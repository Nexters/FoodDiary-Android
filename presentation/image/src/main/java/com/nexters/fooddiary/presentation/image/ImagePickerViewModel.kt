package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.net.Uri
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.domain.usecase.GetFoodPhotosUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

sealed class UploadResult {
    data object Success : UploadResult()
    data class Failure(val error: Throwable? = null) : UploadResult()
}

internal fun nextSelectionAfterToggle(current: Set<Uri>, uri: Uri, maxCount: Int): Set<Uri> =
    if (current.contains(uri)) current - uri
    else if (current.size < maxCount) current + uri
    else current

class ImagePickerViewModel @AssistedInject constructor(
    @Assisted initialState: ImagePickerState,
    @ApplicationContext private val context: Context,
    private val getFoodPhotosUseCase: GetFoodPhotosUseCase,
) : MavericksViewModel<ImagePickerState>(initialState) {

    init {
        updatePermissionState()
    }

    fun onPermissionGranted() {
        setState { copy(hasPermission = true) }
        withState { state ->
            loadImagesForDate(state.filterDate ?: LocalDate.now())
        }
    }

    fun loadPhotos(dateString: String?) {
        val date = dateString?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        setState {
            copy(
                filterDate = date,
                allImageUris = emptyList(),
                foodImageUris = emptyList(),
                isLoading = true,
            )
        }
        withState { state ->
            if (state.hasPermission) {
                loadImagesForDate(date ?: LocalDate.now())
            } else {
                setState { copy(isLoading = false) }
            }
        }
    }

    fun refreshGalleryIfHasPermission() {
        withState { state ->
            if (state.hasPermission) loadImagesForDate(state.filterDate ?: LocalDate.now())
        }
    }

    private fun updatePermissionState() {
        val hasPermission = PermissionUtil.hasMediaPermission(context)
        setState { copy(hasPermission = hasPermission) }
    }

    private fun loadImagesForDate(targetDate: LocalDate) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val result = getFoodPhotosUseCase(targetDate)
                applyLoadedImages(result.foodUris, result.allUris)
            } catch (e: Exception) {
                applyEmptyResult()
            }
        }
    }

    fun toggleImageSelection(uri: Uri) {
        setState { copy(selectedUris = nextSelectionAfterToggle(selectedUris, uri, MAX_SELECTION_COUNT)) }
    }

    fun clearSelection() {
        setState { copy(selectedUris = emptySet()) }
    }

    fun uploadImage(onResult: (UploadResult) -> Unit) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val state = awaitState()
            val urisToUpload = state.selectedUris.map { it.toString() }
            val targetDate = state.filterDate ?: LocalDate.now()
            if (urisToUpload.isEmpty()) {
                onResult(UploadResult.Failure())
                return@launch
            }

            val result = withContext(Dispatchers.Default) {
                runCatching {
                    ImageUploadWorker.enqueue(
                        context = context,
                        targetDate = targetDate,
                        uriStrings = urisToUpload,
                    )
                }
            }

            result
                .onSuccess { onResult(UploadResult.Success) }
                .onFailure { error -> onResult(UploadResult.Failure(error)) }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ImagePickerViewModel, ImagePickerState> {
        override fun create(state: ImagePickerState): ImagePickerViewModel
    }

    companion object : MavericksViewModelFactory<ImagePickerViewModel, ImagePickerState> by hiltMavericksViewModelFactory() {
        const val MAX_SELECTION_COUNT = 10
    }

    private fun applyEmptyResult() {
        setState {
            copy(
                foodImageUris = emptyList(),
                allImageUris = emptyList(),
                isLoading = false
            )
        }
    }

    private fun applyLoadedImages(foodUris: List<String>, allUris: List<String>) {
        setState {
            copy(
                foodImageUris = foodUris.map { Uri.parse(it) },
                allImageUris = allUris.map { Uri.parse(it) },
                isLoading = false
            )
        }
    }

}
