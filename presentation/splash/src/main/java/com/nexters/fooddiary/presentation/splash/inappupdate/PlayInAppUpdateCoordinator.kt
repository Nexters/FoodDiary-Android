package com.nexters.fooddiary.presentation.splash.inappupdate

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import kotlinx.coroutines.tasks.await

class PlayInAppUpdateCoordinator(
    context: Context,
) : InAppUpdateCoordinator {
    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(context)
    private var installStateListener: InstallStateUpdatedListener? = null

    override suspend fun checkForUpdate(
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ): InAppUpdateDecision {
        val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
        return when (InAppUpdatePolicy.decide(appUpdateInfo.toPolicyInput())) {
            InAppUpdateAction.IMMEDIATE -> {
                startUpdate(appUpdateInfo, launcher, AppUpdateType.IMMEDIATE)
                InAppUpdateDecision.Immediate(appUpdateInfo)
            }

            InAppUpdateAction.FLEXIBLE -> {
                startUpdate(appUpdateInfo, launcher, AppUpdateType.FLEXIBLE)
                InAppUpdateDecision.Flexible(appUpdateInfo)
            }

            InAppUpdateAction.COMPLETE_FLEXIBLE -> InAppUpdateDecision.CompleteFlexible
            InAppUpdateAction.NONE -> InAppUpdateDecision.None
        }
    }

    override suspend fun completeFlexibleUpdate(): Result<Unit> = runCatching {
        appUpdateManager.completeUpdate().await()
    }

    override fun registerListener(onDecision: (InAppUpdateDecision) -> Unit) {
        if (installStateListener != null) return

        installStateListener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                onDecision(InAppUpdateDecision.CompleteFlexible)
            }
        }
        appUpdateManager.registerListener(requireNotNull(installStateListener))
    }

    override fun unregisterListener() {
        installStateListener?.let(appUpdateManager::unregisterListener)
        installStateListener = null
    }

    private fun startUpdate(
        appUpdateInfo: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        updateType: Int,
    ) {
        val didStart = appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            launcher,
            AppUpdateOptions.newBuilder(updateType).build()
        )
        check(didStart) { "Failed to start in-app update flow." }
    }

    private fun AppUpdateInfo.toPolicyInput(): InAppUpdatePolicyInput = InAppUpdatePolicyInput(
        updateAvailability = updateAvailability(),
        updatePriority = updatePriority(),
        clientVersionStalenessDays = clientVersionStalenessDays(),
        isImmediateAllowed = isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
        isFlexibleAllowed = isUpdateTypeAllowed(AppUpdateType.FLEXIBLE),
        installStatus = installStatus(),
    )
}
