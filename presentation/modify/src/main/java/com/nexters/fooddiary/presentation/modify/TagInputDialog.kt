package com.nexters.fooddiary.presentation.modify

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nexters.fooddiary.core.ui.component.CommonCircleButton
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.AppTextPrimary
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.AppSurfaceVariant
import com.nexters.fooddiary.core.ui.theme.AppBackground
import com.nexters.fooddiary.core.ui.theme.Color191821

@Composable
internal fun TagInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var value by remember { mutableStateOf("") }
    val placeholder = stringResource(R.string.modify_tag_dialog_placeholder)
    val titleTag = stringResource(R.string.modify_section_tag)
    val cancelText = stringResource(R.string.modify_tag_dialog_cancel)
    val confirmText = stringResource(R.string.modify_tag_dialog_confirm)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(AppBackground, RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = titleTag,
                    style = AppTypography.p15,
                    color = AppTextPrimary,
                )
                StyledInputField(
                    value = value,
                    onValueChange = { value = it },
                    placeholder = placeholder,
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 10.dp,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CommonCircleButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        buttonColors = ButtonDefaults.buttonColors(
                            contentColor = Gray200,
                            containerColor = AppBackground,
                        ),
                        border = BorderStroke(1.dp, AppSurfaceVariant),
                        buttonText = cancelText,
                        contentColor = Color191821,
                    )
                    CommonCircleButton(
                        modifier = Modifier.weight(2f),
                        onClick = {
                            onConfirm(value.trim())
                        },
                        buttonText = confirmText,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun TagInputDialogPreview() {
    TagInputDialog(
        onDismiss = {},
        onConfirm = {},
    )
}
