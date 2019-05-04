package com.racquetbuddy.utils

import android.content.Context
import com.racquetbuddy.businesslogic.DefaultRacquetValues
import com.racquetbuddy.businesslogic.DefaultRacquetValues.CF

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

    // RacquetTensionUtils characteristics

    // Area of the racquet's head
//    private var S: Double = DefaultRacquetValues.DEFAULT_S

    // Density of the string
//    private var rho: Double = DefaultRacquetValues.DEFAULT_RHO

    // Diameter of the string
//    private var d: Double = DefaultRacquetValues.DEFAULT_D

    // Frequency of string vibration
//    private var v: Double = 0.0

    // Needed to calculate tension

    // Strings tension result
//    private var Tx: Double = 0.0

    // Coefficient C0
    private var C0: Double = 0.0

    // Coefficient Cf - received by previously chosen racquet and measure it
//    private var Cf: Double = 0.0

    // Companion class holding racquet details
//    class CF {
//        companion object {
//            const val Tf = 25.0
//            const val vf = 460.0
//            const val rhof = 1.25
//            const val df = 1.27
//            const val Sf = 630.0
//        }
//    }

//    init {
//        this.Cf = (CF.Tf / (CF.vf * CF.vf)) * (1 / (CF.rhof * CF.df * CF.df * CF.Sf))
//    }

    /**
     * @param v the vibration in Hz of the racquet
     */
    fun getStringTension(v: Double): Double {
        C0 = DefaultRacquetValues.DEFAULT_CF * DefaultRacquetValues.DEFAULT_D * DefaultRacquetValues.DEFAULT_D * DefaultRacquetValues.DEFAULT_S
        return C0 * v * v
    }

    /**
     * @param v the vibration in Hz of the racquet
     */
    fun getStringTension(v: Double, S: Double): Double {
        C0 = DefaultRacquetValues.DEFAULT_CF * DefaultRacquetValues.DEFAULT_D * DefaultRacquetValues.DEFAULT_D * S
        return C0 * v * v
    }

    /**
     * @return Tensions in KG
     * @param v the vibration in Hz of the racquet
     */
    fun getStringTension(v: Float, S: Float, d: Float): Double {
        C0 = DefaultRacquetValues.DEFAULT_CF * d * d * S
        return C0 * v * v
    }

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