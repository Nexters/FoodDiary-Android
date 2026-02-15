package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.net.Uri
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.domain.usecase.BatchUploadPhotosUseCase
import com.nexters.fooddiary.domain.usecase.GetTodayFoodPhotosUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

class ImagePickerViewModel @AssistedInject constructor(
    @Assisted initialState: ImagePickerState,
    @ApplicationContext private val context: Context,
    private val getTodayFoodPhotosUseCase: GetTodayFoodPhotosUseCase,
    private val batchUploadPhotosUseCase: BatchUploadPhotosUseCase
) : MavericksViewModel<ImagePickerState>(initialState) {

    init {
        checkPermission()
    }

    private fun checkPermission() {
        val hasPermission = PermissionUtil.hasMediaPermission(context)
        setState { copy(hasPermission = hasPermission) }
        
        if (hasPermission) {
            loadTodayFoodImages()
        }
    }

    fun onPermissionGranted() {
        setState { copy(hasPermission = true) }
        loadTodayFoodImages()
    }

    private fun loadTodayFoodImages() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val result = getTodayFoodPhotosUseCase()
                setState {
                    copy(
                        foodImageUris = result.foodUris.map { Uri.parse(it) },
                        allImageUris = result.allUris.map { Uri.parse(it) },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                setState {
                    copy(
                        foodImageUris = emptyList(),
                        allImageUris = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleImageSelection(uri: Uri) {
        setState {
            val newSet = if (selectedUris.contains(uri)) {
                selectedUris - uri
            } else {
                if (selectedUris.size < MAX_SELECTION_COUNT) selectedUris + uri else selectedUris
            }
            copy(selectedUris = newSet)
        }
    }

    fun clearSelection() {
        setState { copy(selectedUris = emptySet()) }
    }

    fun uploadImage(
        onUploadSuccess: () -> Unit,
        onUploadFailure: () -> Unit
    ) {
        viewModelScope.launch {
            val urisToUpload = awaitState().selectedUris.map { it.toString() }
            if (urisToUpload.isEmpty()) {
                onUploadFailure()
                return@launch
            }
            batchUploadPhotosUseCase(
                date = java.time.LocalDate.now(),
                photoUriStrings = urisToUpload
            )
                .onSuccess { onUploadSuccess() }
                .onFailure { onUploadFailure() }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ImagePickerViewModel, ImagePickerState> {
        override fun create(state: ImagePickerState): ImagePickerViewModel
    }

    companion object : MavericksViewModelFactory<ImagePickerViewModel, ImagePickerState> by hiltMavericksViewModelFactory() {
        const val MAX_SELECTION_COUNT = 10
    }
}
