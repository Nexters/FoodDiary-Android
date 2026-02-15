package com.nexters.fooddiary.presentation.image.di

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.MavericksViewModelComponent
import com.airbnb.mvrx.hilt.ViewModelKey
import com.nexters.fooddiary.presentation.image.ImagePickerViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ImageClassificationViewModel::class)
    fun imageClassificationViewModelFactory(factory: ImageClassificationViewModel.Factory): AssistedViewModelFactory<*, *>
    
    @Binds
    @IntoMap
    @ViewModelKey(ImagePickerViewModel::class)
    fun imagePickerViewModelFactory(factory: ImagePickerViewModel.Factory): AssistedViewModelFactory<*, *>
}