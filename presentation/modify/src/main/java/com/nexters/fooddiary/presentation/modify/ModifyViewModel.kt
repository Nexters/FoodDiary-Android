package com.nexters.fooddiary.presentation.modify

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.model.UpdateDiaryParam
import com.nexters.fooddiary.domain.usecase.diary.DeleteDiaryUseCase
import com.nexters.fooddiary.domain.usecase.diary.GetDiaryByIdUseCase
import com.nexters.fooddiary.domain.usecase.diary.UpdateDiaryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface ModifyEvent {
    data object Saved : ModifyEvent
    data object Deleted : ModifyEvent
}

class ModifyViewModel @AssistedInject constructor(
    @Assisted initialState: ModifyState,
    private val getDiaryByIdUseCase: GetDiaryByIdUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
) : MavericksViewModel<ModifyState>(initialState) {
    private val _events = MutableSharedFlow<ModifyEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ModifyEvent> = _events.asSharedFlow()

    fun selectCategory(category: String) {
        setState { copy(selectedCategory = category) }
    }

    fun updateAddressSearch(query: String) {
        setState { copy(addressSearchQuery = query) }
    }

    fun removeTag(tag: String) {
        setState { copy(tags = tags.filter { it != tag }) }
    }

    fun addTag(tag: String) {
        val trimmed = normalizeTag(tag) ?: return
        setState {
            appendTagIfMissing(tags, trimmed)?.let { updatedTags ->
                copy(tags = updatedTags)
            } ?: this
        }
    }

    fun syncDiaryId(diaryId: String) {
        var shouldFetch = true
        withState { state ->
            shouldFetch = !(state.diaryId == diaryId && state.isInitialSynced)
        }
        if (!shouldFetch) return

        setState {
            if (this.diaryId == diaryId) this
            else copy(diaryId = diaryId, isInitialSynced = false)
        }
        diaryId.toIntOrNull()?.let { id ->
            suspend {
                getDiaryByIdUseCase(id)
            }.execute { result ->
                when (result) {
                    is Success -> {
                        if (this.diaryId != diaryId) return@execute this
                        val entry = result()
                        val entryCategory = entry.category
                        val mergedCategories = entryCategory
                            ?.takeIf { it.isNotBlank() }
                            ?.let { categories + it }
                            ?: categories
                        copy(
                            photoIds = entry.photos.map { it.photoId.toInt() },
                            photoUrls = entry.photos.map { it.imageUrl },
                            coverPhotoId = entry.coverPhotoId.toInt(),
                            selectedCategory = entryCategory?.takeIf { it.isNotBlank() } ?: selectedCategory,
                            categories = mergedCategories,
                            addressLines = entry.location?.let { listOf(it) } ?: emptyList(),
                            roadAddress = entry.location ?: "",
                            restaurantName = entry.restaurantName ?: "",
                            restaurantUrl = entry.mapLink ?: "",
                            note = entry.note ?: "",
                            tags = entry.tags.ifEmpty { tags },
                            isInitialSynced = true,
                        )
                    }
                    else -> this
                }
            }
        }
    }

    fun removePhotoAt(index: Int) {
        withState { state ->
            val result = removePhotoAtState(
                photoIds = state.photoIds,
                photoUrls = state.photoUrls,
                coverPhotoId = state.coverPhotoId,
                index = index,
            )
            setState {
                copy(
                    photoIds = result.photoIds,
                    photoUrls = result.photoUrls,
                    coverPhotoId = result.coverPhotoId,
                )
            }
        }
    }

    fun onSave() {
        withState { state ->
            val id = state.diaryId.toIntOrNull() ?: return@withState
            val param = state.toUpdateDiaryParam()
            suspend {
                updateDiaryUseCase(id, param)
            }.execute { result ->
                when (result) {
                    is Success -> {
                        _events.tryEmit(ModifyEvent.Saved)
                        this
                    }
                    is Fail -> copy(error = ModifyError.Save)
                    else -> this
                }
            }
        }
    }

    fun clearError() {
        setState { copy(error = null) }
    }

    fun onDelete() {
        withState { state ->
            val id = state.diaryId.toIntOrNull() ?: return@withState
            suspend {
                deleteDiaryUseCase(id)
            }.execute { result ->
                if (result is Success) {
                    _events.tryEmit(ModifyEvent.Deleted)
                }
                this
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ModifyViewModel, ModifyState> {
        override fun create(state: ModifyState): ModifyViewModel
    }

    companion object : MavericksViewModelFactory<ModifyViewModel, ModifyState> by hiltMavericksViewModelFactory()
}

internal fun normalizeTag(tag: String): String? =
    tag.trim().takeIf { it.isNotBlank() }

internal fun appendTagIfMissing(tags: List<String>, newTag: String): List<String>? =
    if (newTag in tags) null else tags + newTag

internal data class RemovePhotoStateResult(
    val photoIds: List<Int>,
    val photoUrls: List<String>,
    val coverPhotoId: Int?,
)

internal fun removePhotoAtState(
    photoIds: List<Int>,
    photoUrls: List<String>,
    coverPhotoId: Int?,
    index: Int,
): RemovePhotoStateResult {
    val newPhotoIds = photoIds.filterIndexed { i, _ -> i != index }
    val newPhotoUrls = photoUrls.filterIndexed { i, _ -> i != index }
    val removedPhotoId = photoIds.getOrNull(index)
    val newCoverPhotoId = when {
        removedPhotoId == null -> coverPhotoId
        coverPhotoId == removedPhotoId -> newPhotoIds.firstOrNull()
        else -> coverPhotoId
    }
    return RemovePhotoStateResult(
        photoIds = newPhotoIds,
        photoUrls = newPhotoUrls,
        coverPhotoId = newCoverPhotoId,
    )
}

internal fun ModifyState.toUpdateDiaryParam(): UpdateDiaryParam =
    UpdateDiaryParam(
        category = selectedCategory.takeIf { it.isNotBlank() },
        restaurantName = restaurantName.takeIf { it.isNotBlank() },
        restaurantUrl = restaurantUrl.takeIf { it.isNotBlank() },
        roadAddress = roadAddress.takeIf { it.isNotBlank() }
            ?: addressLines.firstOrNull()?.takeIf { it.isNotBlank() },
        tags = tags.takeIf { it.isNotEmpty() },
        note = note.takeIf { it.isNotBlank() },
        coverPhotoId = coverPhotoId ?: photoIds.firstOrNull(),
        photoIds = photoIds.takeIf { it.isNotEmpty() },
    )
