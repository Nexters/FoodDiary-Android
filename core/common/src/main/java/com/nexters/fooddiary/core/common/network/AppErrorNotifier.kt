package com.nexters.fooddiary.core.common.network

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AppErrorEvent(
    val error: NetworkError,
    val path: String?,
)

interface AppErrorNotifier {
    val events: SharedFlow<AppErrorEvent>
    fun notify(event: AppErrorEvent)
}

@Singleton
class DefaultAppErrorNotifier @Inject constructor() : AppErrorNotifier {
    private val _events = MutableSharedFlow<AppErrorEvent>(
        replay = 0,
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val events: SharedFlow<AppErrorEvent> = _events.asSharedFlow()

    override fun notify(event: AppErrorEvent) {
        _events.tryEmit(event)
    }
}
