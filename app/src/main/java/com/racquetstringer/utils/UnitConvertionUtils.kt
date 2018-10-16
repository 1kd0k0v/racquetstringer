package com.racquetstringer.utils

import java.math.BigDecimal

/**
 * Created by musashiwarrior on 14-Oct-18.
 */
object UnitConvertionUtils {
    fun kiloToPound(kilo: Double): Number {
        return BigDecimal(kilo).times(BigDecimal(2.20462262))
    }

    fun inToCm(inch: Double): Number {
        return BigDecimal(inch) / BigDecimal(0.15500031)
    }

    fun cmToIn(cm: Double): Number {
        return BigDecimal(cm) * BigDecimal(0.15500031)
    }
}