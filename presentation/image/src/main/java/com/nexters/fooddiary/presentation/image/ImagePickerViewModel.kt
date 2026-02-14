package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.net.Uri
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.domain.usecase.GetTodayFoodPhotosUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

class ImagePickerViewModel @AssistedInject constructor(
    @Assisted initialState: ImagePickerState,
    @ApplicationContext private val context: Context,
    private val getTodayFoodPhotosUseCase: GetTodayFoodPhotosUseCase
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
                val uriStrings = getTodayFoodPhotosUseCase()
                val uris = uriStrings.map { Uri.parse(it) }
                setState { 
                    copy(
                        imageUris = uris,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                setState { 
                    copy(
                        imageUris = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleImageSelection(uri: Uri) {
        setState {
            copy(
                selectedUris = if (selectedUris.contains(uri)) {
                    selectedUris - uri
                } else {
                    selectedUris + uri
                }
            )
        }
    }

    fun clearSelection() {
        setState { copy(selectedUris = emptySet()) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ImagePickerViewModel, ImagePickerState> {
        override fun create(state: ImagePickerState): ImagePickerViewModel
    }

    companion object : MavericksViewModelFactory<ImagePickerViewModel, ImagePickerState> by hiltMavericksViewModelFactory()
}
