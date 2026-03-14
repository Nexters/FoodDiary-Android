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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet

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

    fun applySearchResult(
        name: String,
        addressName: String,
        roadAddress: String,
        url: String,
    ) {
        val normalizedName = name.trim()
        val normalizedAddressName = addressName.trim()
        val normalizedRoadAddress = roadAddress.trim()
        val normalizedUrl = url.trim()
        setState {
            copy(
                roadAddress = normalizedRoadAddress,
                addressName = normalizedAddressName,
                restaurantName = normalizedName,
                restaurantUrl = normalizedUrl,
                isAddressManuallyUpdated = true,
            )
        }
    }

    fun removeTag(tag: String) {
        setState { copy(tags = tags.filter { it != tag }.toPersistentList()) }
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
            else copy(
                diaryId = diaryId,
                isInitialSynced = false,
                isAddressManuallyUpdated = false,
            )
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
                            ?.let { categories.toPersistentSet().add(it) }
                            ?: categories
                        val entryTags = entry.tags.toPersistentList()
                        val shouldKeepAddress = isAddressManuallyUpdated
                        copy(
                            photoIds = entry.photos.map { it.photoId.toInt() }.toPersistentList(),
                            photoUrls = entry.photos.map { it.imageUrl }.toPersistentList(),
                            coverPhotoId = entry.coverPhotoId.toInt(),
                            selectedCategory = entryCategory?.takeIf { it.isNotBlank() }.orEmpty(),
                            categories = mergedCategories,
                            roadAddress = if (shouldKeepAddress) roadAddress else entry.roadAddress.orEmpty(),
                            addressName = if (shouldKeepAddress) addressName else entry.addressName.orEmpty(),
                            restaurantName = if (shouldKeepAddress) restaurantName else entry.restaurantName.orEmpty(),
                            restaurantUrl = if (shouldKeepAddress) restaurantUrl else entry.mapLink.orEmpty(),
                            note = entry.note.orEmpty(),
                            tags = entryTags,
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

internal fun appendTagIfMissing(tags: ImmutableList<String>, newTag: String): ImmutableList<String>? =
    if (newTag in tags) null else tags.toPersistentList().add(newTag)

internal data class RemovePhotoStateResult(
    val photoIds: ImmutableList<Int>,
    val photoUrls: ImmutableList<String>,
    val coverPhotoId: Int?,
)

internal fun removePhotoAtState(
    photoIds: ImmutableList<Int>,
    photoUrls: ImmutableList<String>,
    coverPhotoId: Int?,
    index: Int,
): RemovePhotoStateResult {
    val newPhotoIds = photoIds.filterIndexed { i, _ -> i != index }.toPersistentList()
    val newPhotoUrls = photoUrls.filterIndexed { i, _ -> i != index }.toPersistentList()
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
        addressName = addressName.takeIf { it.isNotBlank() },
        roadAddress = roadAddress.takeIf { it.isNotBlank() },
        category = selectedCategory.takeIf { it.isNotBlank() },
        restaurantName = restaurantName.takeIf { it.isNotBlank() },
        restaurantUrl = restaurantUrl.takeIf { it.isNotBlank() },
        tags = tags.takeIf { it.isNotEmpty() },
        note = note.takeIf { it.isNotBlank() },
        coverPhotoId = coverPhotoId ?: photoIds.firstOrNull(),
        photoIds = photoIds.takeIf { it.isNotEmpty() },
    )
