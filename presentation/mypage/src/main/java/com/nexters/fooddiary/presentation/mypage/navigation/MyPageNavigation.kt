package com.nexters.fooddiary.presentation.mypage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.presentation.mypage.MyPageScreen
import kotlinx.serialization.Serializable

@Serializable
object MyPageRoute

enum class WebViewPage {
    TermsOfService,
    PrivacyPolicy
}

fun NavGraphBuilder.myPageScreen(
    navigateToWebView: (WebViewPage) -> Unit = {},
    onShowDialog: (DialogData) -> Unit = {},
    onSignOut: () -> Unit = {},
    onRequireReAuthForDeleteAccount: () -> Unit = {},
    onNavigateToAlarmSettings: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    composable<MyPageRoute> {
        MyPageScreen(
            navigateToWebView = navigateToWebView,
            onShowDialog = onShowDialog,
            onSignOut = onSignOut,
            onRequireReAuthForDeleteAccount = onRequireReAuthForDeleteAccount,
            onNavigateToAlarmSettings = onNavigateToAlarmSettings,
            onBack = onBack
        )
    }
}
