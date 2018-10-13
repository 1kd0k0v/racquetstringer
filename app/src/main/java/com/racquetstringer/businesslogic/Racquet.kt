package com.racquetstringer.businesslogic

/**
 * Created by musashiwarrior on 13-Oct-18.
 */
class Racquet {

    // Strings tension
    private var Tx: Double = 0.0

    //
    private var C0: Double = 0.0

    // Area of the racquet's head
    private var S: Double = 0.0

    // Cf = Tf / Vf * Vf * 1 / rhoF * df * df * Sf
    // C0 = Cf * rho * d * d * S
    // Tx = C0 * Vx * Vx

    /**
     * @param v the vibration in Hz of the racquet
     */
    fun getStringsTension(v: Double): Double {
        return Tx
    }
}