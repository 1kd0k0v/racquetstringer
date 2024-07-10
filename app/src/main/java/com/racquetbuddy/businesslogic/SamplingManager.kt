package com.racquetbuddy.businesslogic

import android.app.Activity
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import com.racquetbuddy.audioanalyzer.SamplingLoop
import com.racquetbuddy.audioanalyzer.SamplingLoop.SoundAnalyzerCallback
import com.racquetbuddy.utils.SharedPrefsUtils
import java.util.concurrent.ArrayBlockingQueue

class SamplingManager private constructor() {

    private val samplingThreads = ArrayList<Thread>()

    private val frequencyListeners = ArrayList<FrequencyListener>()

    private val emptyAudioSamples = ByteArray(1024)

    private object Holder {
        val INSTANCE = SamplingManager()
    }

    companion object {
        val instance: SamplingManager by lazy { Holder.INSTANCE }
    }

    private fun getSamplingLoopInstance(
        activity: Activity,
        visualizerFrameLayout: WaveVisualizer?
    ): SamplingLoop {

        val queueCapacity = SharedPrefsUtils.getQueueCapacity(activity)

        val frequencyQueue = ArrayBlockingQueue<Double>(queueCapacity)

        val minFreq = SharedPrefsUtils.getMinFreq(activity)
        val maxFreq = SharedPrefsUtils.getMaxFreq(activity)
        val dbThreshold = SharedPrefsUtils.getDbThreshold(activity)
        val configuredOccurrenceCount = SharedPrefsUtils.getOccurrenceCount(activity)

        return SamplingLoop(
            SoundAnalyzerCallback { frequency, db, spectrogram ->

                if (frequency > minFreq && frequency < maxFreq && db > dbThreshold) {
                    val count = frequencyQueue.count { it > frequency - 2 && it < frequency + 2 }
                    if (count >= configuredOccurrenceCount) {
                        activity.runOnUiThread {
                            for (listener in frequencyListeners) {
                                listener.getFrequency(frequency.toFloat())
                            }

                            if (spectrogram != null && visualizerFrameLayout != null) {
                                visualizerFrameLayout.setRawAudioBytes(spectrogram)
                            }
                        }
                    } else {
                        if (spectrogram != null && visualizerFrameLayout != null) {
                            activity.runOnUiThread {
                                visualizerFrameLayout.setRawAudioBytes(emptyAudioSamples)
                            }
                        }
                    }

                    if (frequencyQueue.size == queueCapacity) {
                        frequencyQueue.remove()
                    }
                    frequencyQueue.add(frequency)
                } else {
                    if (spectrogram != null && visualizerFrameLayout != null) {
                        activity.runOnUiThread {
                            visualizerFrameLayout.setRawAudioBytes(emptyAudioSamples)
                        }
                    }
                }
            }, activity.resources
        )
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

    fun addFrequencyListener(listener: FrequencyListener) {
        if (!frequencyListeners.contains(listener)) frequencyListeners.add(listener)
    }

    fun removeFrequencyListener(listener: FrequencyListener) {
        if (frequencyListeners.contains(listener)) frequencyListeners.remove(listener)
    }

    interface FrequencyListener {
        fun getFrequency(hz: Float)
    }
}