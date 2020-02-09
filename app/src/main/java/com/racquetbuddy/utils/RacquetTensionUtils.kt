package com.racquetbuddy.utils

import android.content.Context
import com.racquetbuddy.businesslogic.RacquetConstants.CF

/**
 * Created by musashiwarrior on 13-Oct-18.
 *
 * Use metric system for calculations
 *
 * Formulas to calculate tension of the string:
 * Cf = Tf / Vf * Vf * 1 / rhoF * df * df * Sf
 * C0 = Cf * rho * d * d * S
 * Tx = C0 * Vx * Vx
 */
object RacquetTensionUtils {

    /**
     * @return Tension
     * @param v - the vibration in Hz of the racquet
     * @param S - the head size
     * @param d - diameter of the string
     * @param rho - density of the string
     * @param g - stringer style
     * @param f - frame
     * @param p - string pattern
     */
    private fun getStringTension(v: Float, S: Float, d: Float, rho: Float, g: Float, f: Float, p: Float): Double {
        return CF * rho * d * d * S * v * v * g * f * p
    }

    /**
     * @return Tension
     * @param v - the vibration in Hz of the racquet
     * @param S - the head size
     * @param d1 - diameter of the string
     * @param d2 - diameter of the string
     * @param rho1 - density of the string
     * @param rho2 - density of the string
     * @param g - stringer style
     * @param f - frame
     * @param p - string pattern
     */
    private fun getStringTension(v: Float, S: Float, d1: Float, rho1: Float, d2: Float, rho2: Float, g: Float, f: Float, p: Float): Double {
        return g * f * p * CF * ((rho1 * d1 * d1 + rho2 * d2 * d2) / 2) * S * v * v
    }

    fun calculateStringTension(hz: Float, context: Context): Double {
        var headSize = SharedPrefsUtils.getRacquetHeadSize(context)
        if(!SharedPrefsUtils.isHeadImperialUnits(context)) {
            headSize = UnitUtils.cmToIn(headSize).toFloat()
        }

        val stringThickness = SharedPrefsUtils.getStringsThickness(context)
        val stringDensity = StringDataArrayUtils.getStringDensity(SharedPrefsUtils.getStringType(context))
        val stringerStyle = StringDataArrayUtils.getStringerStyle(SharedPrefsUtils.getStringersStyle(context))
        val frame = StringDataArrayUtils.getStringOpeningSize(SharedPrefsUtils.getFrame(context))
        val stringPattern = StringDataArrayUtils.getStringPattern(SharedPrefsUtils.getStringPattern(context))

        return if (SharedPrefsUtils.isStringHybrid(context)) {
            getStringTension(
                    hz,
                    headSize,
                    stringThickness,
                    stringDensity,
                    SharedPrefsUtils.getCrossStringsThickness(context),
                    StringDataArrayUtils.getStringDensity(SharedPrefsUtils.getCrossStringType(context)),
                    stringerStyle,
                    frame,
                    stringPattern
            )
        } else getStringTension(hz,
                headSize,
                stringThickness,
                stringDensity,
                stringerStyle,
                frame,
                stringPattern)
    }

    fun getDisplayTension(tension: Double, context: Context): String {
        return if (SharedPrefsUtils.isTensoinImperialUnits(context)) {
            if (SharedPrefsUtils.isCalibrated(context) && tension != 0.0) {
                NumberFormatUtils.format(UnitUtils.kiloToPound(tension).toFloat() + SharedPrefsUtils.getTensionAdjustment(context))
            } else {
                NumberFormatUtils.format(UnitUtils.kiloToPound(tension))
            }
        } else {
            if (SharedPrefsUtils.isCalibrated(context) && tension != 0.0) {
                NumberFormatUtils.format(tension + SharedPrefsUtils.getTensionAdjustment(context))
            } else {
                NumberFormatUtils.format(tension)
            }
        }
    }
}