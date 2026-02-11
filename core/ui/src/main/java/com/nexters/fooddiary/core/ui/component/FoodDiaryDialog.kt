package com.nexters.fooddiary.core.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray100
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.theme.White
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme

//임시용 커스텀 다이얼로그
@Composable
fun FoodDiaryDialog(
    dialogData: DialogData,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Sd900,
        title = dialogData.title?.let {
            {
                Text(
                    text = it,
                    style = AppTypography.hd18,
                    color = White
                )
            }
        },
        text = {
            Text(
                text = dialogData.message,
                style = AppTypography.p15,
                color = Gray100
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    dialogData.onConfirm?.invoke()
                    onDismissRequest()
                }
            ) {
                Text(
                    text = dialogData.confirmText,
                    style = AppTypography.hd16,
                    color = White
                )
            }
        },
        dismissButton = dialogData.dismissText?.let {
            {
                TextButton(
                    onClick = {
                        dialogData.onDismiss?.invoke()
                        onDismissRequest()
                    }
                ) {
                    Text(
                        text = it,
                        style = AppTypography.hd16,
                        color = Gray400
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun FoodDiaryDialogPreview() {
    FoodDiaryTheme {
        FoodDiaryDialog(
            dialogData = DialogData(
                title = "테스트 타이틀",
                message = "테스트 메시지입니다. 다이얼로그가 잘 보이나요?",
                confirmText = "확인",
                dismissText = "취소"
            ),
            onDismissRequest = {}
        )
    }
}
