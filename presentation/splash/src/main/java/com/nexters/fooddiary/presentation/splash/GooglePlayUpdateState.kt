package com.nexters.fooddiary.presentation.splash

import androidx.compose.runtime.saveable.mapSaver

internal sealed interface GooglePlayUpdateState {
    data object Checking : GooglePlayUpdateState
    data object ReadyToNavigate : GooglePlayUpdateState
    data object WaitingFlexibleDownload : GooglePlayUpdateState
    data object WaitingImmediateResult : GooglePlayUpdateState
    data object ShowingFlexibleCompletionDialog : GooglePlayUpdateState
    data object ImmediateUpdateCanceled : GooglePlayUpdateState
    data object ImmediateUpdateFailed : GooglePlayUpdateState
}

internal val GooglePlayUpdateStateSaver = mapSaver(
    save = { state ->
        when (state) {
            GooglePlayUpdateState.Checking -> mapOf(KEY_TYPE to TYPE_CHECKING)
            GooglePlayUpdateState.ReadyToNavigate -> mapOf(KEY_TYPE to TYPE_READY_TO_NAVIGATE)
            GooglePlayUpdateState.WaitingFlexibleDownload -> mapOf(KEY_TYPE to TYPE_WAITING_FLEXIBLE_DOWNLOAD)
            GooglePlayUpdateState.WaitingImmediateResult -> mapOf(KEY_TYPE to TYPE_WAITING_IMMEDIATE_RESULT)
            GooglePlayUpdateState.ShowingFlexibleCompletionDialog -> mapOf(KEY_TYPE to TYPE_SHOWING_FLEXIBLE_COMPLETION_DIALOG)
            GooglePlayUpdateState.ImmediateUpdateCanceled -> mapOf(KEY_TYPE to TYPE_IMMEDIATE_UPDATE_CANCELED)
            GooglePlayUpdateState.ImmediateUpdateFailed -> mapOf(KEY_TYPE to TYPE_IMMEDIATE_UPDATE_FAILED)
        }
    },
    restore = { savedState ->
        when (savedState[KEY_TYPE]) {
            TYPE_CHECKING -> GooglePlayUpdateState.Checking
            TYPE_READY_TO_NAVIGATE -> GooglePlayUpdateState.ReadyToNavigate
            TYPE_WAITING_FLEXIBLE_DOWNLOAD -> GooglePlayUpdateState.WaitingFlexibleDownload
            TYPE_WAITING_IMMEDIATE_RESULT -> GooglePlayUpdateState.WaitingImmediateResult
            TYPE_SHOWING_FLEXIBLE_COMPLETION_DIALOG -> GooglePlayUpdateState.ShowingFlexibleCompletionDialog
            TYPE_IMMEDIATE_UPDATE_CANCELED -> GooglePlayUpdateState.ImmediateUpdateCanceled
            TYPE_IMMEDIATE_UPDATE_FAILED -> GooglePlayUpdateState.ImmediateUpdateFailed

            else -> GooglePlayUpdateState.Checking
        }
    }
)

private const val KEY_TYPE = "type"
private const val TYPE_CHECKING = "checking"
private const val TYPE_READY_TO_NAVIGATE = "ready_to_navigate"
private const val TYPE_WAITING_FLEXIBLE_DOWNLOAD = "waiting_flexible_download"
private const val TYPE_WAITING_IMMEDIATE_RESULT = "waiting_immediate_result"
private const val TYPE_SHOWING_FLEXIBLE_COMPLETION_DIALOG = "showing_flexible_completion_dialog"
private const val TYPE_IMMEDIATE_UPDATE_CANCELED = "immediate_update_canceled"
private const val TYPE_IMMEDIATE_UPDATE_FAILED = "immediate_update_failed"
