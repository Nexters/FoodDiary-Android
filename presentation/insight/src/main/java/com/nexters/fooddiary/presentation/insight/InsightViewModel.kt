package com.nexters.fooddiary.presentation.insight

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InsightViewModel @AssistedInject constructor(
    @Assisted initialState: InsightState,
) : MavericksViewModel<InsightState>(initialState) {
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<InsightViewModel, InsightState> {
        override fun create(state: InsightState): InsightViewModel
    }

    companion object : MavericksViewModelFactory<InsightViewModel, InsightState> by hiltMavericksViewModelFactory()
}
