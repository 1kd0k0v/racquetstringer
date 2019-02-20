package com.racquetbuddy.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.racquetbuddy.businesslogic.Racquet
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.CalibrateDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitUtils
import kotlinx.android.synthetic.main.calibration_fragment.*

class CalibrationFragment : Fragment(), OnRefreshViewsListener {
    override fun refreshViews() {
        initAdjustmentTextView()
    }

    private val ADJUST_DIALOG_TAG = "ADJUST_DIALOG_TAG"
    private val ADJUST_REQUEST_CODE = 0

    private val samplingManager = SamplingManager.instance

    private var currentAdjustment = 0f

    private var defaultMeasurement: Float = 0f

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

        currentAdjustment = SharedPrefsUtils.getTensionAdjustment(activity!!)

        samplingManager.addMaxAmpListener(object : SamplingManager.MaxAmplitudeListener {
            override fun getMaxAmplitude(amplitude: Float) {

                if (activity != null) {
                    var headSize = SharedPrefsUtils.getRacquetHeadSize(activity!!)
                    if(!SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
                        headSize = UnitUtils.cmToIn(headSize).toFloat()
                    }

                    val tension = Racquet.getStringsTension(amplitude, headSize, SharedPrefsUtils.getStringsDiameter(activity!!), SharedPrefsUtils.getStringDensity(activity!!))
                    val tensionInLb = UnitUtils.kiloToPound(tension).toDouble()

                    defaultMeasurement = tension.toFloat()

                    if (isImperial()) {
                        val units = getString(R.string.tension_lb)
                        fabricModeUnitsTextView.text = units
                        calibrationPersonalModeUnitsTextView.text = units
                        fabricModeTextView.text = NumberFormatUtils.format(tensionInLb)

                        if (isCalibrated()) {
                            personalModeTextView.text = NumberFormatUtils.format(tensionInLb +
                                    currentAdjustment)
                        } else {
                            personalModeTextView.text = NumberFormatUtils.format(tensionInLb)
                        }
                    } else {
                        val units = getString(R.string.tension_kg)
                        fabricModeUnitsTextView.text = units
                        calibrationPersonalModeUnitsTextView.text = units
                        fabricModeTextView.text = NumberFormatUtils.format(tension)

                        if (isCalibrated()) {
                            personalModeTextView.text = NumberFormatUtils.format(tension + currentAdjustment)
                        } else {
                            personalModeTextView.text = NumberFormatUtils.format(tension)
                        }
                    }
                }
            }
        })

        adjustButton.setOnClickListener {
            val dialog = CalibrateDialogFragment.newInstance(getTension())
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, ADJUST_DIALOG_TAG)
        }

        initModeRadioGroup()

        initAdjustmentTextView()

//        restoreDefaultButton.setOnClickListener {
//            SharedPrefsUtils.setCalibrated(activity!!, false)
//            SharedPrefsUtils.setTensionAdjustment(activity!!, 0f)
//            Toast.makeText(activity!!, "Default values restored.", Toast.LENGTH_LONG).show()
//        }
    }

    private fun getTension(): Float {
        return if (isImperial()) {
            UnitUtils.kiloToPound(defaultMeasurement.toDouble()).toFloat()
        } else {
            defaultMeasurement
        }
    }

    private var isPersonalModeSelected: Boolean = true

    private fun initModeRadioGroup() {
        isPersonalModeSelected = isCalibrated()

        if (isPersonalModeSelected) {
            modeRadioGroup.check(R.id.personalRadioButton)
            enableCalibrationLayout()
        } else {
            modeRadioGroup.check(R.id.fabricRadioButton)
            disableCalibrationLayout()
        }

        modeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.personalRadioButton -> {
                    isPersonalModeSelected = true
                    enableCalibrationLayout()
                    SharedPrefsUtils.setTensionAdjustment(activity!!, currentAdjustment)
                    SharedPrefsUtils.setCalibrated(activity!!, true)
                }

                R.id.fabricRadioButton -> {
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

    private fun initAdjustmentTextView() {
        val adjustment = currentAdjustment
        val units = if (SharedPrefsUtils.isTensoinImperialUnits(activity!!)) getString(R.string.tension_lb) else getString(R.string.tension_kg)
        when {
            adjustment == 0f -> currentAdjustmentTextView.text = ""
            adjustment > 0f -> currentAdjustmentTextView.text = getString(R.string.current_adjustment,"+", NumberFormatUtils.format(adjustment), units)
            else -> {
                currentAdjustmentTextView.text = getString(R.string.current_adjustment,"", NumberFormatUtils.format(adjustment), units)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADJUST_REQUEST_CODE) {
            if (resultCode == CalibrateDialogFragment.RESULT_CODE_OK) {
                val adjustment = data?.getFloatExtra(CalibrateDialogFragment.ADJUSTMENT_EXTRA, 0f) ?: 0f
                currentAdjustment = adjustment
                SharedPrefsUtils.setTensionAdjustment(activity!!, currentAdjustment)
                SharedPrefsUtils.setCalibrated(activity!!, true)
            }
        }
    }

    fun isImperial(): Boolean {
        return SharedPrefsUtils.isTensoinImperialUnits(activity!!)
    }

    fun isCalibrated(): Boolean {
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

}
