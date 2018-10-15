package com.racquetstringer.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by musashiwarrior on 14-Oct-18.
 */
object SharedPrefsUtils {

    private const val SHARED_PREFS_UTILS = "SHARED_PREFS_UTILS"
    private const val KEY_IMPERIAL_MEASURE_UNITS = "KEY_IMPERIAL_MEASURE_UNITS"

    private fun getEditor(context: Context) : SharedPreferences.Editor{
        return getSharedPreferences(context).edit()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_UTILS, 0)
    }

    fun areImperialMeasureUnits(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IMPERIAL_MEASURE_UNITS, false)
    }

    fun setImperialMeasureUnits(context: Context, isImperial: Boolean) {
        getEditor(context).putBoolean(KEY_IMPERIAL_MEASURE_UNITS, isImperial).apply()
    }
}