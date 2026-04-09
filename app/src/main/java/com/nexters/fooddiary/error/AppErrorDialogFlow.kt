package com.nexters.fooddiary.error

import android.os.SystemClock
import com.nexters.fooddiary.core.ui.alert.DialogData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

data class DialogEmission(
    val key: String,
    val dialog: DialogData,
)

class DuplicateEventWindowFilter(
    private val windowMillis: Long,
) {
    private var lastKey: String? = null
    private var lastAt = 0L

    fun shouldEmit(key: String, nowMillis: Long): Boolean {
        val isDuplicateInWindow = key == lastKey && nowMillis - lastAt <= windowMillis
        if (isDuplicateInWindow) return false
        lastKey = key
        lastAt = nowMillis
        return true
    }
}

fun Flow<DialogEmission>.suppressDuplicateWithin(
    windowMillis: Long,
    nowProvider: () -> Long = SystemClock::elapsedRealtime,
): Flow<DialogEmission> {
    val duplicateFilter = DuplicateEventWindowFilter(windowMillis)
    return transform { emission ->
        if (duplicateFilter.shouldEmit(emission.key, nowProvider())) {
            emit(emission)
        }
    }
}
