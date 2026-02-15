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
import java.time.LocalDate

class ImagePickerViewModel @AssistedInject constructor(
    @Assisted initialState: ImagePickerState,
    @ApplicationContext private val context: Context,
    private val getTodayFoodPhotosUseCase: GetTodayFoodPhotosUseCase,
    private val batchUploadPhotosUseCase: BatchUploadPhotosUseCase
) : MavericksViewModel<ImagePickerState>(initialState) {

    init {
        syncPermissionAndLoadIfGranted()
    }

    fun onPermissionGranted() {
        setState { copy(hasPermission = true) }
        loadTodayFoodImages()
    }

    fun toggleImageSelection(uri: Uri) {
        setState { copy(selectedUris = nextSelectionAfterToggle(selectedUris, uri)) }
    }

    fun clearSelection() {
        setState { copy(selectedUris = emptySet()) }
    }

    fun uploadImage(onUploadSuccess: () -> Unit, onUploadFailure: () -> Unit) {
        viewModelScope.launch {
            val urisToUpload = selectedUrisAsStrings()
            if (urisToUpload.isEmpty()) {
                onUploadFailure()
                return@launch
            }
            performUpload(urisToUpload, onUploadSuccess, onUploadFailure)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ImagePickerViewModel, ImagePickerState> {
        override fun create(state: ImagePickerState): ImagePickerViewModel
    }

    companion object : MavericksViewModelFactory<ImagePickerViewModel, ImagePickerState> by hiltMavericksViewModelFactory() {
        const val MAX_SELECTION_COUNT = 10
    }

    private fun syncPermissionAndLoadIfGranted() {
        val hasPermission = PermissionUtil.hasMediaPermission(context)
        setState { copy(hasPermission = hasPermission) }
        if (hasPermission) loadTodayFoodImages()
    }

    private fun loadTodayFoodImages() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val result = getTodayFoodPhotosUseCase()
                applyLoadedImages(result.foodUris, result.allUris)
            } catch (e: Exception) {
                applyLoadedImages(emptyList(), emptyList())
            }
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

    private fun nextSelectionAfterToggle(current: Set<Uri>, uri: Uri): Set<Uri> =
        if (current.contains(uri)) current - uri
        else if (current.size < MAX_SELECTION_COUNT) current + uri
        else current

    private suspend fun selectedUrisAsStrings(): List<String> =
        awaitState().selectedUris.map { it.toString() }

    private suspend fun performUpload(
        urisToUpload: List<String>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        batchUploadPhotosUseCase(LocalDate.now(), urisToUpload)
            .onSuccess { onSuccess() }
            .onFailure { onFailure() }
    }
}
