package com.racquetbuddy.businesslogic

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import com.racquetbuddy.audioanalyzer.SamplingLoop
import com.racquetbuddy.audioanalyzer.SamplingLoop.AnalyzerCallback
import com.racquetbuddy.racquetstringer.R

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

    private fun getSamplingLoopInstance(activity: Activity, visualizerFrameLayout: FrameLayout?): SamplingLoop {

        val freqBuffer = arrayListOf<Double>()

        val visualizer = WaveVisualizer(activity)
        if (visualizerFrameLayout != null) {
            visualizer.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            visualizer.setStrokeWidth(1f)
            visualizer.setColor(activity.resources.getColor(R.color.light_green_circle))
            activity.runOnUiThread(object : Thread() {
                override fun run() {
                    visualizerFrameLayout.removeAllViewsInLayout()
                    visualizerFrameLayout.addView(visualizer)
                    visualizerFrameLayout.invalidate()
                }
            })
        }

        return SamplingLoop(
            object: AnalyzerCallback {
                override fun getAmpFreq(frequency: Double) {
                    Log.d("Amplitude", "Amp: $frequency");

                    if (frequency > 400 && frequency < 700) {
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

                        if (freqBuffer.size == 1000) {
                            freqBuffer.clear()
                        }

                        freqBuffer.add(frequency)
                    }
                }

                override fun getSoundSpectrogram(values: ByteArray?) {
                    if (values == null || visualizerFrameLayout == null) return
                    visualizer.setRawAudioBytes(values)
                }


            }, activity.resources)
    }

    fun startSampling(activity: Activity, visualizerFrameLayout: FrameLayout?) {
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