package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.Gray300
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.Sd900

@Composable
fun FoodDiaryDialog(
    dialogData: DialogData,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = dialogData.dismissOnOutsideTouch,
            dismissOnBackPress = dialogData.dismissOnBackPress
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxWidth = 560.dp),
            shape = RoundedCornerShape(24.dp),
            color = Sd900
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                dialogData.title?.let {
                    Text(
                        text = it,
                        style = AppTypography.hd16,
                        color = Gray050
                    )
                }

                Text(
                    text = dialogData.message,
                    style = AppTypography.p12,
                    color = Gray300
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    dialogData.dismissText?.let { dismissText ->
                        DialogActionButton(
                            text = dismissText,
                            modifier = Modifier.weight(1f),
                            textColor = Gray200,
                            backgroundColor = Color.Transparent,
                            borderStroke = BorderStroke(1.dp, Sd800)
                        ) {
                            dialogData.onDismiss?.invoke()
                            onDismissRequest()
                        }
                    }

                    DialogActionButton(
                        text = dialogData.confirmText,
                        modifier = Modifier.weight(if (dialogData.dismissText == null) 1f else 1.7f),
                        textColor = Gray050,
                        backgroundColor = PrimBase
                    ) {
                        dialogData.onConfirm?.invoke()
                        onDismissRequest()
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogActionButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color,
    backgroundColor: Color,
    borderStroke: BorderStroke? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(47.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .then(
                if (borderStroke != null) Modifier.border(
                    borderStroke,
                    RoundedCornerShape(999.dp)
                ) else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.hd16,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun FoodDiaryDialogPreview() {
    FoodDiaryTheme {
        FoodDiaryDialog(
            dialogData = DialogData(
                title = "로그아웃",
                message = "로그아웃을 진행하시겠습니까?",
                confirmText = "로그아웃",
                dismissText = "취소"
            ),
            onDismissRequest = {}
        )
    }
}
