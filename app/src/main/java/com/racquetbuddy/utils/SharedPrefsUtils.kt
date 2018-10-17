package com.racquetbuddy.utils

import android.content.Context
import android.content.SharedPreferences
import com.racquetbuddy.businesslogic.DefaultRacquetValues

/**
 * Created by musashiwarrior on 14-Oct-18.
 */
object SharedPrefsUtils {

    private const val SHARED_PREFS_UTILS = "SHARED_PREFS_UTILS"
    private const val KEY_IMPERIAL_MEASURE_UNITS = "KEY_IMPERIAL_MEASURE_UNITS"
    private const val KEY_SET_FIRST_RUN = "KEY_SET_FIRST_RUN"
    private const val KEY_STRINGS_DIAMETER = "KEY_STRINGS_DIAMETER"
    private const val KEY_HEAD_SIZE = "KEY_HEAD_SIZE"

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

    fun isFirstRun(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_SET_FIRST_RUN, true)
    }

    fun setFirstRun(context: Context, isFirstRun: Boolean) {
        getEditor(context).putBoolean(KEY_SET_FIRST_RUN, isFirstRun).apply()
    }

    fun getRacquetHeadSize(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_HEAD_SIZE, DefaultRacquetValues.DEFAULT_S.toFloat())
    }

    fun setRacquetHeadSize(context: Context, headSize: Double) {
        getEditor(context).putFloat(KEY_HEAD_SIZE, headSize.toFloat()).apply()
    }

    fun getStringsDiameter(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_STRINGS_DIAMETER, DefaultRacquetValues.DEFAULT_D.toFloat())
    }

    fun setStringsDiameter(context: Context, stringsDiameter: Double) {
        getEditor(context).putFloat(KEY_STRINGS_DIAMETER, stringsDiameter.toFloat()).apply()
    }
}