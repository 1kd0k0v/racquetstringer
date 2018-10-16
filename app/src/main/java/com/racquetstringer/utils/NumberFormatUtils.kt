package com.racquetstringer.utils

/**
 * Created by musashiwarrior on 16-Oct-18.
 */
object NumberFormatUtils {
    fun format(number: Number): String {
        val decimalFormat = java.text.DecimalFormat("#.00")
        decimalFormat.roundingMode = java.math.RoundingMode.CEILING
        return decimalFormat.format(number)
    }
}