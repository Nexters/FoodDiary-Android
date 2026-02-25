package com.nexters.fooddiary.presentation.modify

import com.airbnb.mvrx.MavericksState

sealed interface ModifyError {
    data object Save : ModifyError
}

data class ModifyState(
    val diaryId: String = "",
    val isInitialSynced: Boolean = false,
    val selectedCategory: String = "",
    val categories: Set<String> = setOf(),
    val addressSearchQuery: String = "",
    val addressLines: List<String> = emptyList(),
    val roadAddress: String = "",
    val restaurantName: String = "",
    val restaurantUrl: String = "",
    val note: String = "",
    val photoIds: List<Int> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val coverPhotoId: Int? = null,
    val tags: List<String> = emptyList(),
    val error: ModifyError? = null,
) : MavericksState
