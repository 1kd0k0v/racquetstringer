package com.racquetbuddy.utils

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
     * @return Tensions in KG
     * @param v the vibration in Hz of the racquet
     * @param rho the density of the string
     */
    fun getStringTension(v: Float, S: Float, d: Float, rho: Float): Double {
        return CF * rho * d * d * S * v * v
    }

    fun getStringTension(v: Float, S: Float, d1: Float, rho1: Float, d2: Float, rho2: Float): Double {
        return CF * ((rho1 * d1 * d1 + rho2 * d2 * d2) / 2) * S * v * v
    }
}