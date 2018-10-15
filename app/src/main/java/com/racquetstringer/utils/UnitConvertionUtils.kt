package com.racquetstringer.utils

import java.math.BigDecimal

/**
 * Created by musashiwarrior on 14-Oct-18.
 */
object UnitConvertionUtils {
    fun kiloToPound(kilo: BigDecimal): Number {
        return kilo.times(BigDecimal(2.20462262))
    }
}