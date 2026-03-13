package com.nexters.fooddiary.presentation.insight

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.usecase.GetUserInsightsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InsightViewModel @AssistedInject constructor(
    @Assisted initialState: InsightScreenState,
    private val getUserInsightsUseCase: GetUserInsightsUseCase,
) : MavericksViewModel<InsightScreenState>(initialState) {

    init {
        loadInsights()
    }

    fun loadInsights() {
        suspend { getUserInsightsUseCase() }
            .execute { async ->
                async.invoke()?.toInsightScreenState() ?: InsightScreenState()
            }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<InsightViewModel, InsightScreenState> {
        override fun create(state: InsightScreenState): InsightViewModel
    }

    companion object :
        MavericksViewModelFactory<InsightViewModel, InsightScreenState> by hiltMavericksViewModelFactory()
}
