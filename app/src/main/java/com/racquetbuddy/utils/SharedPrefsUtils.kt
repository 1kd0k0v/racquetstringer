package com.racquetbuddy.utils

import android.content.Context
import android.content.SharedPreferences
import com.racquetbuddy.businesslogic.DefaultRacquetValues

/**
 * Created by musashiwarrior on 14-Oct-18.
 */
object SharedPrefsUtils {

    private const val SHARED_PREFS_UTILS = "SHARED_PREFS_UTILS"
    private const val KEY_TENSION_UNITS = "KEY_TENSION_UNITS"
    private const val KEY_RACQUET_UNITS = "KEY_RACQUET_UNITS"
    private const val KEY_SET_FIRST_RUN = "KEY_SET_FIRST_RUN"
    private const val KEY_STRINGS_DIAMETER = "KEY_STRINGS_DIAMETER"
    private const val KEY_HEAD_SIZE = "KEY_HEAD_SIZE"
    private const val KEY_STRING_TYPE = "KEY_STRING_TYPE"
    private const val KEY_CALIBRATED = "KEY_CALIBRATED"
    private const val KEY_TENSION_ADJUSMENT = "KEY_TENSION_ADJUSMENT"
    private const val KEY_STRING_DENSITY = "KEY_STRING_DENSITY"

    private fun getEditor(context: Context) : SharedPreferences.Editor{
        return getSharedPreferences(context).edit()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_UTILS, 0)
    }

    fun isTensoinImperialUnits(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_TENSION_UNITS, false)
    }

    fun setTensionImperialUnits(context: Context, isImperial: Boolean) {
        getEditor(context).putBoolean(KEY_TENSION_UNITS, isImperial).apply()
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

    fun isHeadImperialUnits(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_RACQUET_UNITS, false)
    }

    fun setHeadImperialUnits(context: Context, isImperial: Boolean) {
        getEditor(context).putBoolean(KEY_RACQUET_UNITS, isImperial).apply()
    }

    fun getStringsDiameter(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_STRINGS_DIAMETER, DefaultRacquetValues.DEFAULT_D.toFloat())
    }

    fun setStringsDiameter(context: Context, stringsDiameter: Double) {
        getEditor(context).putFloat(KEY_STRINGS_DIAMETER, stringsDiameter.toFloat()).apply()
    }

    fun setStringType(context: Context, type: Int) {
        getEditor(context).putInt(KEY_STRING_TYPE, type).apply()
    }

    fun getStringType(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_STRING_TYPE, 0)
    }


    fun setCalibrated(context: Context, isCalibrated: Boolean) {
        getEditor(context).putBoolean(KEY_CALIBRATED, isCalibrated).apply()
    }

    fun isCalibrated(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_CALIBRATED, false)
    }

    fun setTensionAdjustment(context: Context, adjustment: Float) {
        getEditor(context).putFloat(KEY_TENSION_ADJUSMENT, adjustment).apply()
    }

    fun getTensionAdjustment(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_TENSION_ADJUSMENT, 0f)
    }

    fun setStringDensity(context: Context, density: Float) {
        getEditor(context).putFloat(KEY_STRING_DENSITY, density).apply()
    }

    fun getStringDensity(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_STRING_DENSITY, DefaultRacquetValues.DEFAULT_RHO.toFloat())
    }
}