package com.nexters.fooddiary.core.ui.alert

sealed interface AppDialogData

data class DialogData(
    val title: String? = null,
    val message: String,
    val confirmText: String = "확인",
    val dismissText: String? = null,
    val dismissOnOutsideTouch: Boolean = true,
    val dismissOnBackPress: Boolean = true,
    val onConfirm: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
) : AppDialogData

data class DeleteAccountDialogData(
    val title: String,
    val message: String,
    val warningItems: List<String>,
    val agreementGuideText: String,
    val agreementText: String,
    val confirmText: String,
    val dismissText: String,
    val onConfirm: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
) : AppDialogData

data class SnackBarData(
    val message: String,
    val iconRes: Int? = null,
    val delayMillis: Long = 2_000L,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)
