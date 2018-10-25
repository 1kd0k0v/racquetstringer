package com.racquetbuddy.businesslogic

import android.app.Activity
import android.content.res.Resources
import android.util.Log
import com.racquetbuddy.audioanalyzer.SamplingLoop

/**
 * Created by musashiwarrior on 24-Oct-18.
 */
class SamplingManager private constructor(){

    val samplingThreads = ArrayList<Thread>()

    val ampListeners = ArrayList<MaxAmplitudeListener>()

    private object Holder {val INSTANCE = SamplingManager()}

    companion object {
        val instance: SamplingManager by lazy {Holder.INSTANCE}
    }

    private fun getSamplingLoopInstance(activity: Activity, resources: Resources): SamplingLoop {

        val ampBuffer = arrayListOf<Double>()

        return SamplingLoop(SamplingLoop.AnalyzerCallback { amplitude ->
            Log.d("Amplitude", "Amp: $amplitude");

            if (amplitude > 400 && amplitude < 700) {
                val found = ampBuffer.find { it > amplitude - 2 && it < amplitude + 2 }
                if (found != null) {
                    activity.runOnUiThread {
                        val avgAmplitude = (found + amplitude) / 2
                        for (listener in ampListeners) {
                            listener.getMaxAmplitude(avgAmplitude)
                        }
                    }
                    ampBuffer.clear()
                }

                if (ampBuffer.size == 1000) {
                    ampBuffer.clear()
                }

                ampBuffer.add(amplitude)
            }
        }, resources)
    }

    fun startSampling(activity: Activity, resources: Resources) {
        val samplingLoop = getSamplingLoopInstance(activity, resources)
        samplingThreads.add(samplingLoop)
        samplingLoop.start()
    }

    fun stopSampling() {
        for (thread in samplingThreads) {
            (thread as SamplingLoop).finish()
        }
    }

    fun addMaxAmpListener(listener: MaxAmplitudeListener) {
        if (!ampListeners.contains(listener)) ampListeners.add(listener)
    }

    fun remoteMaxAmpListener(listener: MaxAmplitudeListener) {
        if (ampListeners.contains(listener)) ampListeners.remove(listener)
    }

    interface MaxAmplitudeListener {
        fun getMaxAmplitude(amplitude: Double)
    }
}