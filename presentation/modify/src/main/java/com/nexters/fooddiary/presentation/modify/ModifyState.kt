package com.nexters.fooddiary.presentation.modify

import com.airbnb.mvrx.MavericksState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

sealed interface ModifyError {
    data object Save : ModifyError
}

data class ModifyState(
    val diaryId: String = "",
    val isInitialSynced: Boolean = false,
    val selectedCategory: String = "",
    val categories: ImmutableSet<String> = persistentSetOf(),
    val isAddressManuallyUpdated: Boolean = false,
    val roadAddress: String = "",
    val addressName: String = "",
    val restaurantName: String = "",
    val restaurantUrl: String = "",
    val note: String = "",
    val photoIds: ImmutableList<Int> = persistentListOf(),
    val photoUrls: ImmutableList<String> = persistentListOf(),
    val coverPhotoId: Int? = null,
    val tags: ImmutableList<String> = persistentListOf(),
    val error: ModifyError? = null,
) : MavericksState
