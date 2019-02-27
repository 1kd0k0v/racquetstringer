package com.racquetbuddy.businesslogic

import android.app.Activity
import android.util.Log
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import com.racquetbuddy.audioanalyzer.SamplingLoop
import com.racquetbuddy.audioanalyzer.SamplingLoop.SoundAnalyzerCallback
import com.racquetbuddy.utils.SharedPrefsUtils

/**
 * Created by musashiwarrior on 24-Oct-18.
 */
class SamplingManager private constructor(){

    private val samplingThreads = ArrayList<Thread>()

    val ampListeners = ArrayList<MaxAmplitudeListener>()

    val emptyAudioSamples = ByteArray(1024)

    private object Holder {val INSTANCE = SamplingManager()}

    companion object {
        val instance: SamplingManager by lazy {Holder.INSTANCE}
    }

    private fun getSamplingLoopInstance(activity: Activity, visualizerFrameLayout: WaveVisualizer?): SamplingLoop {

        val freqBuffer = mutableListOf<Double>()

        val minFreq = SharedPrefsUtils.getMinFreq(activity)
        val maxFreq = SharedPrefsUtils.getMaxFreq(activity)
        val dbThreshold = SharedPrefsUtils.getDbThreshld(activity)

        return SamplingLoop(
            object: SoundAnalyzerCallback {

                override fun onSoundDataReceived(frequency: Double, db: Double, spectrogram: ByteArray?) {
                    Log.d("Amplitude", "Amp: $frequency")

                    if (frequency > minFreq && frequency < maxFreq && db > dbThreshold) {
                        val found = freqBuffer.find { it > frequency - 2 && it < frequency + 2 }
                        if (found != null) {
                            activity.runOnUiThread {
                                val avgAmplitude = (found + frequency) / 2
                                for (listener in ampListeners) {
                                    listener.getMaxAmplitude(avgAmplitude.toFloat())
                                }
                            }
                            freqBuffer.clear()
                        }

                        if (freqBuffer.size >= 1000) {
                            freqBuffer.clear()
                        }

                        freqBuffer.add(frequency)

                        if (spectrogram == null || visualizerFrameLayout == null) return
                        activity.runOnUiThread {
                            visualizerFrameLayout.setRawAudioBytes(spectrogram)
                        }
                    } else {
                        if (spectrogram == null || visualizerFrameLayout == null) return
                        activity.runOnUiThread {
                            visualizerFrameLayout.setRawAudioBytes(emptyAudioSamples)
                        }
                    }


                }
            }, activity.resources)
    }

    fun startSampling(activity: Activity, visualizerFrameLayout: WaveVisualizer?) {
        val samplingLoop = getSamplingLoopInstance(activity, visualizerFrameLayout)
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

    fun removeMaxAmpListener(listener: MaxAmplitudeListener) {
        if (ampListeners.contains(listener)) ampListeners.remove(listener)
    }

    interface MaxAmplitudeListener {
        fun getMaxAmplitude(amplitude: Float)
    }
}