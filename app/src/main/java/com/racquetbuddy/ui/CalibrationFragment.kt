package com.racquetbuddy.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.racquetbuddy.businesslogic.Racquet
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.AdjustDialogFragment
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitConvertionUtils
import kotlinx.android.synthetic.main.calibration_fragment.*

class CalibrationFragment : Fragment() {

    private val ADJUST_DIALOG_TAG = "ADJUST_DIALOG_TAG"

    private val samplingManager = SamplingManager.instance

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

        samplingManager.addMaxAmpListener(object : SamplingManager.MaxAmplitudeListener {
            override fun getMaxAmplitude(amplitude: Double) {

                if (activity != null) {
                    var headSize = SharedPrefsUtils.getRacquetHeadSize(activity!!).toDouble()
                    if(!SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
                        headSize = UnitConvertionUtils.cmToIn(headSize).toDouble()
                    }

                    val tension = Racquet.getStringsTension(amplitude, headSize, SharedPrefsUtils.getStringsDiameter(activity!!).toDouble())
                    val tensionInLb = UnitConvertionUtils.kiloToPound(tension).toDouble()

                    defaultMeasurement = tension.toFloat()

                    if (isImperial()) {
                        defaultUnitsTensionTextView.text = getString(R.string.tension_lb)
                        unitsTensionTextVIew.text = getString(R.string.tension_lb)
                        defaultMeasurementTextView.text = NumberFormatUtils.format(tensionInLb)

                        if (isCalibrated()) {
                            calibratedTextView.text = NumberFormatUtils.format(UnitConvertionUtils.kiloToPound(tension +
                                    SharedPrefsUtils.getTensionAdjustmentKg(activity!!).toDouble()))
                        } else {
                            calibratedTextView.text = NumberFormatUtils.format(tensionInLb)
                        }
                    } else {
                        defaultUnitsTensionTextView.text = getString(R.string.tension_kg)
                        unitsTensionTextVIew.text = getString(R.string.tension_kg)
                        defaultMeasurementTextView.text = NumberFormatUtils.format(tension)

                        if (isCalibrated()) {
                            calibratedTextView.text = NumberFormatUtils.format(tension + SharedPrefsUtils.getTensionAdjustmentKg(activity!!))
                        } else {
                            calibratedTextView.text = NumberFormatUtils.format(tension)
                        }
                    }
                }
            }
        })

        adjustButton.setOnClickListener {
            val dialog = AdjustDialogFragment.newInstance(defaultMeasurement)
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, ADJUST_DIALOG_TAG)
        }

        restoreDefaultButton.setOnClickListener {
            SharedPrefsUtils.setCalibrated(activity!!, false)
            SharedPrefsUtils.setTensionAdjustmentKg(activity!!, 0f)
            Toast.makeText(activity!!, "Default values restored.", Toast.LENGTH_LONG).show()
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
        samplingManager.startSampling(activity!!, resources)
    }

    override fun onPause() {
        super.onPause()
        samplingManager.stopSampling()
    }

}
