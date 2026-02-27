package com.nexters.fooddiary.push

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class AnalysisCompleteSyncEvent(
    val diaryDate: String,
)

object PushSyncEventBus {
    private val _analysisCompleteEvents = MutableSharedFlow<AnalysisCompleteSyncEvent>(
        extraBufferCapacity = 32
    )
    val analysisCompleteEvents: SharedFlow<AnalysisCompleteSyncEvent> =
        _analysisCompleteEvents.asSharedFlow()

    fun publishAnalysisComplete(diaryDate: String) {
        if (diaryDate.isBlank()) return
        _analysisCompleteEvents.tryEmit(AnalysisCompleteSyncEvent(diaryDate = diaryDate))
    }
}

