package com.nexters.fooddiary.presentation.splash.inappupdate

import com.google.android.play.core.appupdate.AppUpdateInfo

sealed interface InitialInAppUpdateDecision {
    data object None : InitialInAppUpdateDecision
    data object CompleteFlexible : InitialInAppUpdateDecision
    data class Immediate(val appUpdateInfo: AppUpdateInfo) : InitialInAppUpdateDecision
    data class Flexible(val appUpdateInfo: AppUpdateInfo) : InitialInAppUpdateDecision
}

sealed interface FlexibleInstallStateDecision {
    data object Downloaded : FlexibleInstallStateDecision
    data object Canceled : FlexibleInstallStateDecision
    data object Failed : FlexibleInstallStateDecision
}
