package com.nexters.fooddiary.navigation

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
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)
