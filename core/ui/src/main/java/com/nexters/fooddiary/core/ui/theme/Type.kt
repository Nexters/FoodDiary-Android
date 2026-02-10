package com.nexters.fooddiary.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 31.2.sp,
        letterSpacing = (-0.36).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 23.4.sp,
        letterSpacing = (-0.27).sp
    ),
    displaySmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.8.sp,
        letterSpacing = (-0.24).sp
    ),

    bodyLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.27).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 19.5.sp,
        letterSpacing = (-0.225).sp
    ),
    bodySmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = (-0.21).sp
    ),
    labelSmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = (-0.18).sp
    )
)

val Typography.hd24: TextStyle get() = displayLarge
val Typography.hd18: TextStyle get() = displayMedium
val Typography.hd16: TextStyle get() = displaySmall
val Typography.p18: TextStyle get() = bodyLarge
val Typography.p15: TextStyle get() = bodyMedium
val Typography.p14: TextStyle get() = bodySmall
val Typography.p12: TextStyle get() = labelSmall
