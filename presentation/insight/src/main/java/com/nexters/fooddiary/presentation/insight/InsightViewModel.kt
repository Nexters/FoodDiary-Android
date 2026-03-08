package com.nexters.fooddiary.presentation.insight

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InsightViewModel @AssistedInject constructor(
    @Assisted initialState: InsightScreenState,
) : MavericksViewModel<InsightScreenState>(initialState) {

    init {
        loadInsights()
    }

    fun loadInsights() {
        // TODO: /me/insights 응답이 연결되면 donutCard 외 다른 카드들도 타입별 UI model 필드로 분리해 매핑한다.
        setState {
            copy(donutCard = sampleInsightDonutCard())
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<InsightViewModel, InsightScreenState> {
        override fun create(state: InsightScreenState): InsightViewModel
    }

    companion object :
        MavericksViewModelFactory<InsightViewModel, InsightScreenState> by hiltMavericksViewModelFactory()
}
