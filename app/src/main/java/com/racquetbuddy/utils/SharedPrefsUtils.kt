package com.racquetbuddy.utils

import android.content.Context
import android.content.SharedPreferences
import com.racquetbuddy.businesslogic.RacquetConstants
import com.racquetbuddy.utils.StringDataArrayUtils.STRINGING_TYPE_DEFAULT
import com.racquetbuddy.utils.StringDataArrayUtils.STRING_OPENING_SIZE_DEFAULT
import com.racquetbuddy.utils.StringDataArrayUtils.STRING_PATTERN_DEFAULT
import com.racquetbuddy.utils.StringDataArrayUtils.STRING_TYPE_DEFAULT

object SharedPrefsUtils {

    private const val SHARED_PREFS_UTILS = "SHARED_PREFS_UTILS"
    private const val KEY_TENSION_UNITS = "KEY_TENSION_UNITS"
    private const val KEY_RACQUET_UNITS = "KEY_RACQUET_UNITS"
    private const val KEY_SET_FIRST_RUN = "KEY_SET_FIRST_RUN"
    private const val KEY_HEAD_SIZE = "KEY_HEAD_SIZE"
    private const val KEY_CALIBRATED = "KEY_CALIBRATED"
    private const val KEY_TENSION_ADJUSMENT = "KEY_TENSION_ADJUSMENT"
    private const val KEY_MIN_FREQ = "KEY_MIN_FREQ"
    private const val KEY_MAX_FREQ = "KEY_MAX_FREQ"
    private const val KEY_DB_THRESHOLD = "KEY_DB_THRESHOLD"
    private const val KEY_OCCURENCE_COUNT = "KEY_OCCURENCE_COUNT"
    private const val KEY_QUEUE_CAPACITY = "KEY_QUEUE_CAPACITY"

    private const val KEY_STRING_TYPE = "KEY_STRING_TYPE"
    private const val KEY_STRINGS_THICKNESS = "KEY_STRINGS_THICKNESS"
    private const val KEY_STRING_PATTERN = "KEY_STRING_PATTERN"
    private const val KEY_STRINGERS_STYLE = "KEY_STRINGERS_STYLE"
    private const val KEY_GROMMET = "KEY_GROMMET"

    private const val KEY_CROSS_STRING_TYPE = "KEY_CROSS_STRING_TYPE"
    private const val KEY_CROSS_STRING_THICKNESS = "KEY_CROSS_STRING_THICKNESS"

    private const val KEY_IS_HYBRID_STRING = "KEY_IS_HYBRID_STRING"



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
        val firstRun = getSharedPreferences(context).getBoolean(KEY_SET_FIRST_RUN, true)
        if (firstRun) {
            setFirstRun(context, false)
        }
        return firstRun
    }

    private fun setFirstRun(context: Context, isFirstRun: Boolean) {
        getEditor(context).putBoolean(KEY_SET_FIRST_RUN, isFirstRun).apply()
    }

    fun getRacquetHeadSize(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_HEAD_SIZE, RacquetConstants.S.toFloat())
    }

    fun setRacquetHeadSize(context: Context, headSize: Double) {
        getEditor(context).putFloat(KEY_HEAD_SIZE, headSize.toFloat()).apply()
    }

    fun isHeadImperialUnits(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_RACQUET_UNITS, true)
    }

    fun setHeadImperialUnits(context: Context, isImperial: Boolean) {
        getEditor(context).putBoolean(KEY_RACQUET_UNITS, isImperial).apply()
    }

    fun getStringsThickness(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_STRINGS_THICKNESS, RacquetConstants.D)
    }

    fun setStringsThickness(context: Context, stringsDiameter: Float) {
        getEditor(context).putFloat(KEY_STRINGS_THICKNESS, stringsDiameter).apply()
    }

    fun setStringType(context: Context, type: Int) {
        getEditor(context).putInt(KEY_STRING_TYPE, type).apply()
    }

    fun getStringType(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_STRING_TYPE, STRING_TYPE_DEFAULT)
    }

    fun setStringPattern(context: Context, type: Int) {
        getEditor(context).putInt(KEY_STRING_PATTERN, type).apply()
    }

    fun getStringPattern(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_STRING_PATTERN, STRING_PATTERN_DEFAULT)
    }

    fun setStringOpeningSize(context: Context, type: Int) {
        getEditor(context).putInt(KEY_GROMMET, type).apply()
    }

    fun getFrame(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_GROMMET, STRING_OPENING_SIZE_DEFAULT)
    }

    fun setStringersStyle(context: Context, type: Int) {
        getEditor(context).putInt(KEY_STRINGERS_STYLE, type).apply()
    }

    fun getStringersStyle(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_STRINGERS_STYLE, STRINGING_TYPE_DEFAULT)
    }

    fun getCrossStringsThickness(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_CROSS_STRING_THICKNESS, RacquetConstants.D)
    }

    fun setCrossStringsThickness(context: Context, stringsDiameter: Float) {
        getEditor(context).putFloat(KEY_CROSS_STRING_THICKNESS, stringsDiameter).apply()
    }

    fun setCrossStringType(context: Context, type: Int) {
        getEditor(context).putInt(KEY_CROSS_STRING_TYPE, type).apply()
    }

    fun getCrossStringType(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_CROSS_STRING_TYPE, 0)
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

    fun setMinFreq(context: Context, density: Float) {
        getEditor(context).putFloat(KEY_MIN_FREQ, density).apply()
    }

    fun getMinFreq(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_MIN_FREQ, RacquetConstants.MIN_FREQ)
    }

    fun setMaxFreq(context: Context, density: Float) {
        getEditor(context).putFloat(KEY_MAX_FREQ, density).apply()
    }

    fun getMaxFreq(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_MAX_FREQ, RacquetConstants.MAX_FREQ)
    }

    fun setDbThreshold(context: Context, density: Float) {
        getEditor(context).putFloat(KEY_DB_THRESHOLD, density).apply()
    }

    fun getDbThreshold(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_DB_THRESHOLD, RacquetConstants.DB_THRESHOLD)
    }

    fun setOccurrenceCount(context: Context, count: Int) {
        getEditor(context).putInt(KEY_OCCURENCE_COUNT, count).apply()
    }

    fun getOccurrenceCount(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_OCCURENCE_COUNT, RacquetConstants.OCCURRENCE_COUNT)
    }

    fun setQueueCapacity(context: Context, capacity: Int) {
        getEditor(context).putInt(KEY_QUEUE_CAPACITY, capacity).apply()
    }

    fun getQueueCapacity(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_QUEUE_CAPACITY, RacquetConstants.QUEUE_CAPACITY)
    }

    fun setStringHybrid(context: Context, hybrid: Boolean) {
        getEditor(context).putBoolean(KEY_IS_HYBRID_STRING, hybrid).apply()
    }

    fun isStringHybrid(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_HYBRID_STRING, false)
    }
}