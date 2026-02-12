package com.nexters.fooddiary.presentation.image

import android.net.Uri
import com.airbnb.mvrx.MavericksState
import java.util.Collections.emptyList
import java.util.Collections.emptySet

data class ImagePickerState(
    val imageUris: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val selectedUris: Set<Uri> = emptySet()
) : MavericksState
