package com.nexters.fooddiary.presentation.splash.inappupdate

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

interface InAppUpdateCoordinator {
    suspend fun checkForUpdate(
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ): InAppUpdateDecision

    suspend fun completeFlexibleUpdate(): Result<Unit>

    fun registerListener(onDecision: (InAppUpdateDecision) -> Unit)

    fun unregisterListener()
}
