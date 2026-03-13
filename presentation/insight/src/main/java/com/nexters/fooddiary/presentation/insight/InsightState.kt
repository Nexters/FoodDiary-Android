package com.nexters.fooddiary.presentation.insight

import com.airbnb.mvrx.MavericksState

data class InsightState(
    val peakMealTime: String = "19:00",
) : MavericksState
