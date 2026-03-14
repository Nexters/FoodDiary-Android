package com.nexters.fooddiary.presentation.insight.di

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.MavericksViewModelComponent
import com.airbnb.mvrx.hilt.ViewModelKey
import com.nexters.fooddiary.presentation.insight.InsightViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(InsightViewModel::class)
    fun insightViewModelFactory(factory: InsightViewModel.Factory): AssistedViewModelFactory<*, *>
}
