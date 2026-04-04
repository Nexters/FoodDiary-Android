package com.nexters.fooddiary.presentation.splash.inappupdate

import com.google.android.play.core.appupdate.AppUpdateInfo

sealed interface InAppUpdateDecision {
    data object None : InAppUpdateDecision
    data object CompleteFlexible : InAppUpdateDecision
    data class Immediate(val appUpdateInfo: AppUpdateInfo) : InAppUpdateDecision
    data class Flexible(val appUpdateInfo: AppUpdateInfo) : InAppUpdateDecision
}
