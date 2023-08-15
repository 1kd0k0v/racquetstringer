package com.racquetbuddy.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.CalibrateDialogFragment
import com.racquetbuddy.ui.dialog.CalibrationInstructionsDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.RacquetTensionUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitUtils
import kotlinx.android.synthetic.main.calibration_fragment.*

class CalibrationFragment : Fragment(), OnRefreshViewsListener {

    private val INSTRUCTIONS_DIALOG_FRAGMENT_TAG = "INSTRUCTIONS_DIALOG_FRAGMENT_TAG"

    override fun refreshViews() {
        refreshCalibrationViews()
    }

    private val ADJUST_DIALOG_TAG = "ADJUST_DIALOG_TAG"
    private val ADJUST_REQUEST_CODE = 0

    private val samplingManager = SamplingManager.instance

    private var currentCalibration = 0f

    private var factoryMeasurement: Float = 0f
    private var currentFrequency: Float = 0f

    companion object {
        fun newInstance() = CalibrationFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.calibration_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        currentCalibration = SharedPrefsUtils.getTensionAdjustment(activity!!)

        samplingManager.addFrequencyListener(object : SamplingManager.FrequencyListener {
            override fun getFrequency(hz: Float) {
                if (activity == null) return

                displayTension(hz)
            }
        })

        calibrationButton.setOnClickListener {
            val dialog = CalibrateDialogFragment.newInstance(getTension())
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, ADJUST_DIALOG_TAG)
        }

        initModeRadioGroup()

        refreshCalibrationViews()

        displayTension(0f)

        readMoreTextView.setOnClickListener {
            showInstructionsDialog()
        }
    }

    private fun displayTension(hz: Float) {

        currentFrequency = hz

        calibrationButton.isEnabled = hz != 0f

        val tension = RacquetTensionUtils.calculateStringTension(hz, context!!)

        factoryMeasurement = tension.toFloat()

        if (isImperial()) {
            val tensionInLb = UnitUtils.kiloToPound(tension).toDouble()
            val units = getString(R.string.tension_lb)
            factoryModeUnitsTextView.text = units
            calibrationPersonalModeUnitsTextView.text = units
            factoryModeTextView.text = NumberFormatUtils.format(tensionInLb)

            if (isCalibrated()) {
                personalModeTextView.text = NumberFormatUtils.format(tensionInLb +
                        currentCalibration)
            } else {
                personalModeTextView.text = NumberFormatUtils.format(tensionInLb)
            }
        } else {
            val units = getString(R.string.tension_kg)
            factoryModeUnitsTextView.text = units
            calibrationPersonalModeUnitsTextView.text = units
            factoryModeTextView.text = NumberFormatUtils.format(tension)

            if (isCalibrated()) {
                personalModeTextView.text = NumberFormatUtils.format(tension +
                        currentCalibration)
            } else {
                personalModeTextView.text = NumberFormatUtils.format(tension)
            }
        }
    }

    private fun getTension(): Float {
        return if (isImperial()) {
            UnitUtils.kiloToPound(factoryMeasurement.toDouble()).toFloat()
        } else {
            factoryMeasurement
        }
    }

    private var isPersonalModeSelected: Boolean = true

    private fun initModeRadioGroup() {
        isPersonalModeSelected = isCalibrated()

        if (isPersonalModeSelected) {
            modeRadioGroup.check(R.id.personalRadioButton)
            enableCalibrationLayout()
        } else {
            modeRadioGroup.check(R.id.factoryRadioButton)
            disableCalibrationLayout()
        }

        modeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.personalRadioButton -> {
                    isPersonalModeSelected = true
                    enableCalibrationLayout()
                    SharedPrefsUtils.setTensionAdjustment(activity!!, currentCalibration)
                    SharedPrefsUtils.setCalibrated(activity!!, true)
                }

                R.id.factoryRadioButton -> {
                    isPersonalModeSelected = false
                    disableCalibrationLayout()
                    SharedPrefsUtils.setCalibrated(activity!!, false)
                }
            }
        }
    }

    private fun enableCalibrationLayout() {
        personalModeCalibrationLayout.visibility = View.VISIBLE
    }

    private fun disableCalibrationLayout() {
        personalModeCalibrationLayout.visibility = View.INVISIBLE
    }

    private fun refreshCalibrationViews() {
        val calibration = currentCalibration
        val units = if (SharedPrefsUtils.isTensoinImperialUnits(activity!!)) getString(R.string.tension_lb) else getString(R.string.tension_kg)
        when {
            calibration == 0f -> tv_calibration.text = ""
            calibration > 0f -> tv_calibration.text = getString(R.string.current_adjustment,"+", NumberFormatUtils.format(calibration), units)
            else -> {
                tv_calibration.text = getString(R.string.current_adjustment,"", NumberFormatUtils.format(calibration), units)
            }
        }

        displayTension(currentFrequency)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADJUST_REQUEST_CODE) {
            if (resultCode == CalibrateDialogFragment.RESULT_CODE_OK) {
                val adjustment = data?.getFloatExtra(CalibrateDialogFragment.ADJUSTMENT_EXTRA, 0f) ?: 0f
                currentCalibration = adjustment
                SharedPrefsUtils.setTensionAdjustment(activity!!, currentCalibration)
                SharedPrefsUtils.setCalibrated(activity!!, true)
            }
        }
    }

    private fun isImperial(): Boolean {
        return SharedPrefsUtils.isTensoinImperialUnits(activity!!)
    }

    private fun isCalibrated(): Boolean {
        return SharedPrefsUtils.isCalibrated(activity!!)
    }

    override fun onResume() {
        super.onResume()
        samplingManager.startSampling(activity!!, visualizerFrameLayout2)
    }

    override fun onPause() {
        super.onPause()
        samplingManager.stopSampling()
    }

    private fun showInstructionsDialog() {
        val dialog = CalibrationInstructionsDialogFragment()
        dialog.setTargetFragment(this, 0)
        dialog.show(fragmentManager, INSTRUCTIONS_DIALOG_FRAGMENT_TAG)
    }

}
