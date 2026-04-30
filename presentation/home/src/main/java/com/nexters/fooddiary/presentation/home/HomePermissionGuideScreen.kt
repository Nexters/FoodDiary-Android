package com.nexters.fooddiary.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.core.common.permission.PermissionUtil.MediaAccessState
import com.nexters.fooddiary.core.ui.component.CommonCircleButton
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.R as coreR

@Composable
internal fun HomePermissionGuideScreen(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
    onPermissionGranted: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when (permissionGuideResultAction(PermissionUtil.getMediaAccessState(context))) {
            PermissionGuideResultAction.COMPLETE -> onPermissionGranted()
            PermissionGuideResultAction.OPEN_SETTINGS -> onOpenSettings()
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (PermissionUtil.hasMediaPermission(context)) {
                    onPermissionGranted()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
        ) {
            Spacer(modifier = Modifier.height(116.dp))
            Text(
                text = stringResource(R.string.home_permission_guide_title),
                style = AppTypography.hd18,
                color = Gray050,
            )
            Spacer(modifier = Modifier.height(44.dp))
            Text(
                text = stringResource(R.string.home_permission_guide_section_title),
                style = AppTypography.p14,
                color = Gray400,
            )
            Spacer(modifier = Modifier.height(26.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Sd900)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_permission_photo),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = stringResource(R.string.home_permission_guide_photo_label),
                        style = AppTypography.p15,
                        color = Gray050,
                    )
                    Spacer(modifier = Modifier.width(28.dp))
                    Text(
                        text = stringResource(R.string.home_permission_guide_photo_description),
                        style = AppTypography.p15,
                        color = Gray400,
                    )
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = stringResource(R.string.home_permission_guide_footnote),
                style = AppTypography.p12,
                color = Gray400,
            )
            Spacer(modifier = Modifier.weight(1f))
            CommonCircleButton(
                onClick = {
                    when (permissionGuideEntryAction(PermissionUtil.getMediaAccessState(context))) {
                        PermissionGuideEntryAction.REQUEST_PERMISSION -> {
                            permissionLauncher.launch(PermissionUtil.getRequiredMediaPermissions())
                        }
                        PermissionGuideEntryAction.OPEN_SETTINGS -> onOpenSettings()
                        PermissionGuideEntryAction.COMPLETE -> onPermissionGranted()
                    }
                },
                buttonText = stringResource(R.string.home_permission_guide_primary_action),
                buttonColors = ButtonDefaults.buttonColors(containerColor = PrimBase),
                contentColor = Gray050,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(49.dp)
                    .navigationBarsPadding(),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

internal enum class PermissionGuideEntryAction {
    REQUEST_PERMISSION,
    OPEN_SETTINGS,
    COMPLETE,
}

internal enum class PermissionGuideResultAction {
    OPEN_SETTINGS,
    COMPLETE,
}

internal fun permissionGuideEntryAction(mediaAccessState: MediaAccessState): PermissionGuideEntryAction =
    when (mediaAccessState) {
        MediaAccessState.DENIED -> PermissionGuideEntryAction.REQUEST_PERMISSION
        MediaAccessState.PARTIAL -> PermissionGuideEntryAction.OPEN_SETTINGS
        MediaAccessState.FULL -> PermissionGuideEntryAction.COMPLETE
    }

internal fun permissionGuideResultAction(mediaAccessState: MediaAccessState): PermissionGuideResultAction =
    when (mediaAccessState) {
        MediaAccessState.FULL -> PermissionGuideResultAction.COMPLETE
        MediaAccessState.PARTIAL,
        MediaAccessState.DENIED,
        -> PermissionGuideResultAction.OPEN_SETTINGS
    }
