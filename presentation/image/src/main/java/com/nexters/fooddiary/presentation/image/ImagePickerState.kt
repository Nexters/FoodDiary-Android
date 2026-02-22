package com.nexters.fooddiary.presentation.image

import android.net.Uri
import com.airbnb.mvrx.MavericksState

data class ImagePickerState(
    val foodImageUris: List<Uri> = emptyList(),
    val allImageUris: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val selectedUris: Set<Uri> = emptySet()
) : MavericksState
