package com.nexters.fooddiary.presentation.splash.inappupdate

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

interface InAppUpdateCoordinator {
    suspend fun checkForUpdate(
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ): InitialInAppUpdateDecision

    suspend fun completeFlexibleUpdate(): Result<Unit>

    fun registerFlexibleInstallStateListener(onDecision: (FlexibleInstallStateDecision) -> Unit)

    fun unregisterFlexibleInstallStateListener()
}
