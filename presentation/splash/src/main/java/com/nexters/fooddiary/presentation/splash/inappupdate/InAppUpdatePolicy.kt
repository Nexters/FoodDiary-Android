package com.nexters.fooddiary.presentation.splash.inappupdate

import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

data class InAppUpdatePolicyInput(
    val updateAvailability: Int,
    val updatePriority: Int,
    val clientVersionStalenessDays: Int?,
    val isImmediateAllowed: Boolean,
    val isFlexibleAllowed: Boolean,
    val installStatus: Int,
)

enum class InAppUpdateAction {
    NONE,
    FLEXIBLE,
    IMMEDIATE,
    COMPLETE_FLEXIBLE,
}

object InAppUpdatePolicy {
    private const val IMMEDIATE_PRIORITY_THRESHOLD = 4
    private const val IMMEDIATE_STALENESS_THRESHOLD_DAYS = 7

    fun decide(input: InAppUpdatePolicyInput): InAppUpdateAction {
        if (input.installStatus == InstallStatus.DOWNLOADED) {
            return InAppUpdateAction.COMPLETE_FLEXIBLE
        }

        if (
            input.updateAvailability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
            input.isImmediateAllowed
        ) {
            return InAppUpdateAction.IMMEDIATE
        }

        if (input.updateAvailability != UpdateAvailability.UPDATE_AVAILABLE) {
            return InAppUpdateAction.NONE
        }

        val shouldUseImmediate = input.updatePriority >= IMMEDIATE_PRIORITY_THRESHOLD ||
            (input.clientVersionStalenessDays ?: -1) >= IMMEDIATE_STALENESS_THRESHOLD_DAYS

        return when {
            shouldUseImmediate && input.isImmediateAllowed -> InAppUpdateAction.IMMEDIATE
            input.isFlexibleAllowed -> InAppUpdateAction.FLEXIBLE
            else -> InAppUpdateAction.NONE
        }
    }
}
