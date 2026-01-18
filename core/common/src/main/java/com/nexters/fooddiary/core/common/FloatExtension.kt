package com.nexters.fooddiary.core.common

fun Float.toPercentageString(decimalPlaces: Int = 1): String =
    "%.${decimalPlaces}f%%".format(this * 100)
