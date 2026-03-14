package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.net.Uri
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID

internal fun nextSelectionAfterToggle(current: Set<Uri>, uri: Uri, maxCount: Int): Set<Uri> =
    if (current.contains(uri)) current - uri
    else if (current.size < maxCount) current + uri
    else current

class ImagePickerViewModel @AssistedInject constructor(
    @Assisted initialState: ImagePickerState,
    @ApplicationContext private val context: Context,
    private val getFoodPhotosUseCase: GetFoodPhotosUseCase,
) : MavericksViewModel<ImagePickerState>(initialState) {
    private val workManager by lazy { WorkManager.getInstance(context) }

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
                isUploading = false,
                uploadSucceededDate = null,
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

    fun uploadImage() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val state = awaitState()
            val urisToUpload = state.selectedUris.map { it.toString() }
            val targetDate = state.filterDate ?: LocalDate.now()
            if (urisToUpload.isEmpty() || state.isUploading) {
                return@launch
            }
            setState { copy(isUploading = true) }

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
                .onSuccess { requestId -> observeUploadCompletion(requestId, targetDate) }
                .onFailure { setState { copy(isUploading = false) } }
        }
    }

    fun consumeUploadSuccess() {
        setState { copy(uploadSucceededDate = null) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ImagePickerViewModel, ImagePickerState> {
        override fun create(state: ImagePickerState): ImagePickerViewModel
    }

    companion object : MavericksViewModelFactory<ImagePickerViewModel, ImagePickerState> by hiltMavericksViewModelFactory() {
        const val MAX_SELECTION_COUNT = 10
    }

    private fun observeUploadCompletion(requestId: UUID, targetDate: LocalDate) {
        viewModelScope.launch {
            val workInfo = workManager
                .getWorkInfoByIdFlow(requestId)
                .filterNotNull()
                .first { it.state.isFinished }

            when (workInfo.state) {
                WorkInfo.State.SUCCEEDED -> {
                    setState {
                        copy(
                            isUploading = false,
                            uploadSucceededDate = targetDate,
                        )
                    }
                }
                else -> {
                    setState { copy(isUploading = false) }
                }
            }
        }
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
