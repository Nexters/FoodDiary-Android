package com.nexters.fooddiary.presentation.home

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.component.CommonCircleButton
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.R as coreR

@Composable
internal fun HomePermissionBlockedScreen(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    ) {
        Image(
            imageVector = androidx.compose.ui.graphics.vector.ImageVector.vectorResource(coreR.drawable.ic_back),
            contentDescription = stringResource(R.string.home_permission_back),
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 22.dp)
                .clickable { backDispatcher?.onBackPressed() },
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(322.dp))
            Text(
                text = stringResource(R.string.home_permission_blocked_title),
                style = AppTypography.p15,
                color = Gray050,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(40.dp))
            CommonCircleButton(
                onClick = onOpenSettings,
                buttonText = stringResource(R.string.home_permission_blocked_primary_action),
                buttonColors = ButtonDefaults.buttonColors(containerColor = PrimBase),
                contentColor = Gray050,
                modifier = Modifier
                    .padding(horizontal = 70.dp)
                    .fillMaxWidth()
                    .widthIn(max = 220.dp)
                    .height(49.dp)
                    .navigationBarsPadding(),
            )
        }
    }
}
