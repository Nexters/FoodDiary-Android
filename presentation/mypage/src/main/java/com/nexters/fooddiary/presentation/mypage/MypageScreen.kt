package com.nexters.fooddiary.presentation.mypage

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.common.ContextExtension.getAppVersionName
import com.nexters.fooddiary.core.common.R.string
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.component.DetailScreenHeader
import com.nexters.fooddiary.core.ui.theme.Color363347
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.domain.model.DeleteAccountError
import com.nexters.fooddiary.domain.model.DeleteAccountException
import com.nexters.fooddiary.presentation.mypage.navigation.WebViewPage

@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    navigateToWebView: (WebViewPage) -> Unit = {},
    onSignOut: () -> Unit = {},
    onRequireReAuthForDeleteAccount: () -> Unit = {},
    onNavigateToAlarmSettings: () -> Unit = {},
    viewModel: MyPageViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val recentLoginRequiredMessage = stringResource(string.my_page_recent_login_required)
    val deleteInProgressMessage = stringResource(string.my_page_delete_in_progress)
    val deleteFailedMessage = stringResource(string.my_page_delete_failed)

    LaunchedEffect(state.signOutResult) {
        if (state.signOutResult is Success) {
            onSignOut()
            viewModel.resetSignOutResult()
        }
    }

    LaunchedEffect(state.deleteAccountResult) {
        when (val result = state.deleteAccountResult) {
            is Success -> {
                onSignOut()
                viewModel.resetDeleteAccountResult()
            }

            is Fail -> {
                val error = (result.error as? DeleteAccountException)?.error

                val message = when (error) {
                    is DeleteAccountError.RecentLoginRequired -> {
                        onRequireReAuthForDeleteAccount()
                        recentLoginRequiredMessage
                    }

                    is DeleteAccountError.NoUserSignedIn,
                    is DeleteAccountError.Unknown,
                    null -> {
                        deleteFailedMessage
                    }
                }

                Toast
                    .makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    )
                    .show()

                viewModel.resetDeleteAccountResult()
            }

            else -> Unit
        }
    }

    MyPageScreen(
        modifier = modifier,
        state = state,
        navigateToWebView = navigateToWebView,
        onSIgnOut = viewModel::signOut,
        onDeleteAccount = viewModel::deleteAccount,
        deleteInProgressMessage = deleteInProgressMessage,
        onNavigateToAlarmSettings = onNavigateToAlarmSettings
    )
}

@Composable
internal fun MyPageScreen(
    modifier: Modifier = Modifier,
    state: MyPageState,
    navigateToWebView: (WebViewPage) -> Unit = {},
    onSIgnOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    deleteInProgressMessage: String = "",
    onNavigateToAlarmSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = SdBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            DetailScreenHeader {
                Text(
                    text = stringResource(string.my_page_title),
                    color = Gray050,
                    modifier = Modifier
                )
            }
            ProfileCard(
                modifier = Modifier,
                nickName = state.nickName
            )
            MyPageSection(
                iconId = drawable.ic_alert,
                sectionNameId = string.my_page_section_alert
            ) {
                MyPageSubMenu(
                    menuName = stringResource(string.my_page_menu_set_alarm),
                    onClick = onNavigateToAlarmSettings
                )
            }
            MyPageSection(
                iconId = drawable.ic_setting,
                sectionNameId = string.my_page_section_setting
            ) {
                MyPageSubMenu(
                    menuName = stringResource(
                        string.my_page_menu_app_version,
                        if (LocalInspectionMode.current) "1.0.0" else context.getAppVersionName()
                    )
                )
                MyPageSubMenu(
                    menuName = stringResource(string.my_page_menu_terms_of_service),
                    onClick = { navigateToWebView(WebViewPage.TermsOfService) })
                MyPageSubMenu(
                    menuName = stringResource(string.my_page_menu_privacy_policy),
                    onClick = { navigateToWebView(WebViewPage.PrivacyPolicy) })
            }
            MyPageSection() {
                MyPageSubMenu(menuName = stringResource(string.my_page_menu_logout), onClick = onSIgnOut)
            }
            Spacer(modifier = Modifier.padding(bottom = 80.dp))
        }
        
        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clickable {
                    Toast
                        .makeText(
                            context,
                            deleteInProgressMessage.ifEmpty { context.getString(string.my_page_delete_in_progress) },
                            Toast.LENGTH_SHORT
                        )
                        .show()
                    onDeleteAccount()
                }
                .padding(28.dp)
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
            text = stringResource(string.my_page_sign_out),
            style = TextStyle(
                textDecoration = TextDecoration.Underline
            ),
            color = Gray050
        )
    }
}

@Composable
internal fun ProfileCard(
    modifier: Modifier = Modifier,
    nickName: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color363347)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(drawable.img_profile), contentDescription = "")
        // todo : 닉네임 font style p12로 변경 필요
        Text(
            text = stringResource(string.my_page_nick_name_description),
            color = Gray050,
            modifier = modifier
        )
        Text(
            text = stringResource(string.my_page_nick_name, nickName),
            color = Gray050,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = modifier.padding(top = 7.dp)
        )
    }
}

@Composable
internal fun MyPageSection(
    modifier: Modifier = Modifier,
    iconId: Int = 0,
    sectionNameId: Int = 0,
    content: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)
    ) {
            if(iconId != 0 && sectionNameId != 0) Row(
            modifier = modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(iconId),
                contentDescription = "alert_section"
            )
            Text(
                text = stringResource(sectionNameId),
                color = Gray050
            )
        }
        Column(
            modifier = modifier.clip(RoundedCornerShape(10.dp))
        ) {
            content()
        }
    }
}

@Composable
internal fun MyPageSubMenu(
    modifier: Modifier = Modifier,
    menuName: String = "",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color363347)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = menuName, color = Gray050)
        Image(painter = painterResource(drawable.ic_next), contentDescription = "next_button")
    }
}

@Composable
@Preview
fun MyPageScreenPreview() {
    MyPageScreen(
        state = MyPageState(
            nickName = "홍길동"
        ),
        deleteInProgressMessage = "탈퇴를 진행하고 있어요…"
    )
}