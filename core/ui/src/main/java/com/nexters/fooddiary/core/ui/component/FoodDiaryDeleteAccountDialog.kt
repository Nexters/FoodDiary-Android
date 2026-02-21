package com.nexters.fooddiary.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nexters.fooddiary.core.ui.R
import com.nexters.fooddiary.core.ui.alert.DeleteAccountDialogData
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.FoodDiaryTheme
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.Gray300
import com.nexters.fooddiary.core.ui.theme.Gray500
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.Sd900

@Composable
fun FoodDiaryDeleteAccountDialog(
    dialogData: DeleteAccountDialogData,
    onDismissRequest: () -> Unit
) {
    var isAgreed by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Sd900
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = dialogData.title,
                    style = AppTypography.hd16,
                    color = Gray050
                )

                Text(
                    text = dialogData.message,
                    style = AppTypography.p12,
                    color = Gray300
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Sd800.copy(alpha = 0.3f))
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                ) {
                    dialogData.warningItems.forEach { warningItem ->
                        Text(
                            text = warningItem,
                            style = AppTypography.p12,
                            color = Gray200
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dialogData.agreementGuideText,
                        style = AppTypography.p12,
                        color = Gray200
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Sd800)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAgreed = !isAgreed },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (isAgreed) {
                                    R.drawable.ic_check_state_on
                                } else {
                                    R.drawable.ic_check_state_off
                                }
                            ),
                            contentDescription = dialogData.agreementText,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = dialogData.agreementText,
                            style = AppTypography.p12,
                            color = Gray050
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DeleteAccountDialogActionButton(
                        text = dialogData.dismissText,
                        modifier = Modifier.weight(1f),
                        textColor = Gray200,
                        backgroundColor = Color.Transparent,
                        borderStroke = BorderStroke(1.dp, Sd800)
                    ) {
                        dialogData.onDismiss?.invoke()
                        onDismissRequest()
                    }

                    DeleteAccountDialogActionButton(
                        text = dialogData.confirmText,
                        modifier = Modifier.weight(1.7f),
                        textColor = if (isAgreed) Gray050 else Gray500,
                        backgroundColor = if (isAgreed) PrimBase else Sd800,
                        enabled = isAgreed
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
private fun DeleteAccountDialogActionButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color,
    backgroundColor: Color,
    enabled: Boolean = true,
    borderStroke: BorderStroke? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(47.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .then(
                if (borderStroke != null) {
                    Modifier.border(borderStroke, RoundedCornerShape(999.dp))
                } else {
                    Modifier
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
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
private fun FoodDiaryDeleteAccountDialogPreview() {
    FoodDiaryTheme {
        FoodDiaryDeleteAccountDialog(
            dialogData = DeleteAccountDialogData(
                title = "탈퇴",
                message = "탈퇴를 진행하시겠습니까?",
                warningItems = listOf(
                    "1.탈퇴 시 모든 데이터가 삭제됩니다.",
                    "2.탈퇴 시 한달동안 재가입이 어렵습니다."
                ),
                agreementGuideText = "위 사항에 동의하시면 아래 동의해주세요.",
                agreementText = "위 사항에 동의합니다.",
                confirmText = "탈퇴하기",
                dismissText = "취소"
            ),
            onDismissRequest = {}
        )
    }
}
