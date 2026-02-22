package com.nexters.fooddiary.presentation.modify

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.theme.White

private val InputBg = Sd900
private val DefaultInputShape = RoundedCornerShape(10.dp)

@Composable
internal fun StyledInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    singleLine: Boolean = true,
    shape: Shape = DefaultInputShape,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(shape)
            .fillMaxWidth(),
        textStyle = AppTypography.p15.copy(color = Gray050),
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                placeholder = @Composable {
                    Text(
                        text = placeholder,
                        style = AppTypography.p15,
                        color = Gray600,
                    )
                },
                trailingIcon = trailingIcon,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBg,
                    unfocusedContainerColor = InputBg,
                    disabledContainerColor = InputBg,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Gray050,
                    unfocusedTextColor = Gray050,
                    cursorColor = White,
                ),
                contentPadding = contentPadding,
            )
        },
    )
}
