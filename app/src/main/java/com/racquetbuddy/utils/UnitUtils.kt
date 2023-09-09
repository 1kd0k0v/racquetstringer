package com.racquetbuddy.utils

import android.app.Activity
import com.racquetbuddy.racquetstringer.R
import java.math.BigDecimal

object UnitUtils {
    fun kiloToPound(kilo: Double): Number {
        return BigDecimal(kilo).times(BigDecimal(2.20462262))
    }

    fun cmToIn(cm: Float): Number {
        return BigDecimal(cm.toDouble()) * BigDecimal(0.15500031)
    }

    fun getUnits(activity: Activity): String {
        return if (SharedPrefsUtils.isTensoinImperialUnits(activity))
            activity.getString(R.string.tension_lb)
        else
            activity.getString(R.string.tension_kg)
    }
}