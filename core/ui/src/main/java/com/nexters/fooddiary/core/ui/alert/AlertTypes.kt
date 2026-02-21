package com.nexters.fooddiary.core.ui.alert

data class DialogData(
    val title: String? = null,
    val message: String,
    val confirmText: String = "확인",
    val dismissText: String? = null,
    val onConfirm: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
)

data class SnackBarData(
    val message: String,
    val iconRes: Int? = null,
    val delayMillis: Long = 2_000L,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)
