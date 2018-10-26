package com.racquetbuddy.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.racquetbuddy.businesslogic.Racquet
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.AdjustDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitConvertionUtils
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

        currentAdjustment = SharedPrefsUtils.getTensionAdjustmentKg(activity!!)

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
                        fabricModeUnitsTextView.text = getString(R.string.tension_lb)
                        personalModeUnitsTextView.text = getString(R.string.tension_lb)
                        fabricModeTextView.text = NumberFormatUtils.format(tensionInLb)

                        if (isCalibrated()) {
                            personalModeTextView.text = NumberFormatUtils.format(UnitConvertionUtils.kiloToPound(tension +
                                    currentAdjustment))
                        } else {
                            personalModeTextView.text = NumberFormatUtils.format(tensionInLb)
                        }
                    } else {
                        fabricModeUnitsTextView.text = getString(R.string.tension_kg)
                        personalModeUnitsTextView.text = getString(R.string.tension_kg)
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
            val dialog = AdjustDialogFragment.newInstance(defaultMeasurement)
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, ADJUST_DIALOG_TAG)
        }

        initAdjustmentTextView()

//        restoreDefaultButton.setOnClickListener {
//            SharedPrefsUtils.setCalibrated(activity!!, false)
//            SharedPrefsUtils.setTensionAdjustmentKg(activity!!, 0f)
//            Toast.makeText(activity!!, "Default values restored.", Toast.LENGTH_LONG).show()
//        }
    }

    private fun initAdjustmentTextView() {
        val adjustment = currentAdjustment
        when {
            adjustment == 0f -> currentAdjustmentTextView.text = ""
            adjustment > 0f -> currentAdjustmentTextView.text = getString(R.string.current_adjustment,"+", NumberFormatUtils.format(adjustment))
            else -> {
                currentAdjustmentTextView.text = getString(R.string.current_adjustment,"-", NumberFormatUtils.format(adjustment))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADJUST_REQUEST_CODE) {
            if (resultCode == AdjustDialogFragment.RESULT_CODE_OK) {

                val adjustment = data?.getFloatExtra(AdjustDialogFragment.ADJUSTMENT_EXTRA, 0f) ?: 0f
                if (adjustment != 0f) {
                    currentAdjustment = adjustment
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save -> {
                SharedPrefsUtils.setTensionAdjustmentKg(activity!!, currentAdjustment)
                Toast.makeText(activity, getString(R.string.adjustment_saved, currentAdjustment, getString(R.string.tension_kg)), Toast.LENGTH_LONG).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
