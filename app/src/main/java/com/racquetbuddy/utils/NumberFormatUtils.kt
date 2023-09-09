package com.racquetbuddy.utils

object NumberFormatUtils {
    fun format(number: Number): String {
        val decimalFormat = java.text.DecimalFormat("0.0")
        return decimalFormat.format(number)
    }

    fun formatNoTrailingZeros(number: Number): String {
        val decimalFormat = java.text.DecimalFormat("#.##")
        return decimalFormat.format(number)
    }

    fun round(number: Number): String {
        val decimalFormat = java.text.DecimalFormat("#")
        decimalFormat.roundingMode = java.math.RoundingMode.CEILING
        return decimalFormat.format(number)
    }
}