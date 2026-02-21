package com.nexters.fooddiary.presentation.modify

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
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

class ModifyViewModel @AssistedInject constructor(
    @Assisted initialState: ModifyState,
    private val getDiaryByIdUseCase: GetDiaryByIdUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
) : MavericksViewModel<ModifyState>(initialState) {

    fun selectCategory(category: String) {
        setState { copy(selectedCategory = category) }
    }

    fun updateAddressSearch(query: String) {
        setState { copy(addressSearchQuery = query) }
    }

    fun removeTag(tag: String) {
        setState { copy(tags = tags.filter { it != tag }) }
    }

    fun syncDiaryId(diaryId: String) {
        setState { copy(diaryId = diaryId) }
        diaryId.toIntOrNull()?.let { id ->
            suspend {
                getDiaryByIdUseCase(id)
            }.execute { result ->
                when (result) {
                    is Success -> {
                        val entry = result()
                        copy(
                            photoIds = entry.photos.map { it.photoId.toInt() },
                            photoUrls = entry.photos.map { it.imageUrl },
                            coverPhotoId = entry.coverPhotoId.toInt(),
                            selectedCategory = entry.category ?: selectedCategory,
                            addressLines = entry.location?.let { listOf(it) } ?: emptyList(),
                            roadAddress = entry.location ?: "",
                            restaurantName = entry.restaurantName ?: "",
                            restaurantUrl = entry.mapLink ?: "",
                            note = entry.note ?: "",
                            tags = entry.tags.ifEmpty { tags },
                        )
                    }
                    else -> this
                }
            }
        }
    }

    fun removePhotoAt(index: Int) {
        setState {
            copy(
                photoIds = photoIds.filterIndexed { i, _ -> i != index },
                photoUrls = photoUrls.filterIndexed { i, _ -> i != index },
            )
        }
    }

    fun onSave(onSuccess: () -> Unit = {}) {
        withState { state ->
            val id = state.diaryId.toIntOrNull() ?: return@withState
            val param = UpdateDiaryParam(
                category = state.selectedCategory.takeIf { it.isNotBlank() },
                restaurantName = state.restaurantName.takeIf { it.isNotBlank() },
                restaurantUrl = state.restaurantUrl.takeIf { it.isNotBlank() },
                roadAddress = state.roadAddress.takeIf { it.isNotBlank() }
                    ?: state.addressLines.firstOrNull()?.takeIf { it.isNotBlank() },
                tags = state.tags.takeIf { it.isNotEmpty() },
                note = state.note.takeIf { it.isNotBlank() },
                coverPhotoId = state.coverPhotoId ?: state.photoIds.firstOrNull(),
                photoIds = state.photoIds.takeIf { it.isNotEmpty() },
            )
            suspend {
                updateDiaryUseCase(id, param)
            }.execute { result ->
                if (result is Success) onSuccess()
                this
            }
        }
    }

    fun onDelete(onSuccess: () -> Unit = {}) {
        withState { state ->
            val id = state.diaryId.toIntOrNull() ?: return@withState
            suspend {
                deleteDiaryUseCase(id)
            }.execute { result ->
                if (result is Success) onSuccess()
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
