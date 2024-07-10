package com.racquetbuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.utils.SharedPrefsUtils
import kotlinx.android.synthetic.main.fragment_configuration.applyButton
import kotlinx.android.synthetic.main.fragment_configuration.dbSeekBar
import kotlinx.android.synthetic.main.fragment_configuration.dbTextView
import kotlinx.android.synthetic.main.fragment_configuration.maxFreqSeekBar
import kotlinx.android.synthetic.main.fragment_configuration.maxFreqTextView
import kotlinx.android.synthetic.main.fragment_configuration.minFreqSeekBar
import kotlinx.android.synthetic.main.fragment_configuration.minFreqTextView
import kotlinx.android.synthetic.main.fragment_configuration.occurenceCountSeekBar
import kotlinx.android.synthetic.main.fragment_configuration.occurrenceCountTextView
import kotlinx.android.synthetic.main.fragment_configuration.queueCapacitySeekBar
import kotlinx.android.synthetic.main.fragment_configuration.queueCapacityTextView

class ConfigurationFragment : Fragment() {
    var minFreq = 0
    var maxFreq = 0
    var dbThreshold = 0
    var occurrences = 0
    var queueCapacity = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ConfigurationFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        minFreq = SharedPrefsUtils.getMinFreq(context!!).toInt()

        minFreqSeekBar.progress = minFreq
        minFreqTextView.text = minFreq.toString()
        minFreqSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                minFreqTextView.text = progress.toString()
                minFreq = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        maxFreq = SharedPrefsUtils.getMaxFreq(context!!).toInt()
        maxFreqSeekBar.progress = maxFreq
        maxFreqTextView.text = maxFreq.toString()
        maxFreqSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                maxFreqTextView.text = progress.toString()
                maxFreq = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        dbThreshold = SharedPrefsUtils.getDbThreshold(context!!).toInt()
        dbSeekBar.progress = dbThreshold + 600
        dbTextView.text = dbThreshold.toString()
        dbSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dbTextView.text = (progress - 600).toString()
                dbThreshold = progress - 600
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        occurrences = SharedPrefsUtils.getOccurrenceCount(context!!)
        occurenceCountSeekBar.progress = occurrences
        occurrenceCountTextView.text = occurrences.toString()
        occurenceCountSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                occurrenceCountTextView.text = progress.toString()
                occurrences = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        queueCapacity = SharedPrefsUtils.getQueueCapacity(context!!)
        queueCapacitySeekBar.progress = queueCapacity
        queueCapacityTextView.text = queueCapacity.toString()
        queueCapacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                queueCapacityTextView.text = progress.toString()
                queueCapacity = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        applyButton.setOnClickListener {
            SharedPrefsUtils.setMinFreq(context!!, minFreq.toFloat())
            SharedPrefsUtils.setMaxFreq(context!!, maxFreq.toFloat())
            SharedPrefsUtils.setDbThreshold(context!!, dbThreshold.toFloat())
            SharedPrefsUtils.setOccurrenceCount(context!!, occurrences)
            SharedPrefsUtils.setQueueCapacity(context!!, queueCapacity)
        }

    }
}
