package com.racquetbuddy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.racquetbuddy.businesslogic.Racquet
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.ui.dialog.InstructionsDialogFragment
import com.racquetbuddy.ui.dialog.StringDiameterDialogFragment
import com.racquetbuddy.ui.dialog.StringTypeDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.StringTypeUtils
import com.racquetbuddy.utils.UnitUtils
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment(), OnRefreshViewsListener {

    private val RECORD_AUDIO_CODE = 0
    private val HEAD_SIZE_DIALOG_TAG = "HEAD_SIZE_DIALOG_TAG"
    private val STRINGS_DIAMETER_DIALOG_TAG = "STRINGS_DIAMETER_DIALOG_TAG"
    private val STRING_TYPE_DIALOG_TAG = "STRING_TYPE_DIALOG_TAG"
    private val INSTRUCTIONS_DIALOG_FRAGMENT_TAG = "INSTRUCTIONS_DIALOG_FRAGMENT_TAG"

    private var currentHz: Float = 0f

    private val handler: Handler = Handler();

    private val samplingManager = SamplingManager.instance

    override fun onPause() {
        super.onPause()
        samplingManager.stopSampling()
    }

    override fun onResume() {
        super.onResume()
        startSampling()
    }

    private fun startSampling() {
        samplingManager.addMaxAmpListener(object : SamplingManager.MaxAmplitudeListener {
            override fun getMaxAmplitude(amplitude: Float) {
                if (activity == null) return

                handler.removeCallbacks(clearDisplayTensionRunnable)
                displayTension(amplitude)
                handler.postDelayed(clearDisplayTensionRunnable, 1000)
                currentHz = amplitude
            }
        })
        samplingManager.startSampling(activity!!, visualizerFrameLayout)
    }

    val clearDisplayTensionRunnable = Runnable {
        displayTension(0f)
    }

    private fun displayTension(hz: Float) {
        if (activity == null) return

        var headSize = SharedPrefsUtils.getRacquetHeadSize(activity!!)
        if(!SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
            headSize = UnitUtils.cmToIn(headSize).toFloat()
        }

        personalModeUnitsTextView.text = UnitUtils.getUnits(activity!!)

        val tension = Racquet.getStringsTension(hz, headSize, SharedPrefsUtils.getStringsDiameter(activity!!), SharedPrefsUtils.getStringDensity(activity!!))

        displayTensionTextView.text =
        if (SharedPrefsUtils.isTensoinImperialUnits(activity!!)) {
            if (SharedPrefsUtils.isCalibrated(activity!!) && hz != 0f) {
                NumberFormatUtils.format(UnitUtils.kiloToPound(tension).toFloat() + SharedPrefsUtils.getTensionAdjustment(activity!!))
            } else {
                NumberFormatUtils.format(UnitUtils.kiloToPound(tension))
            }
        } else {
            if (SharedPrefsUtils.isCalibrated(activity!!) && hz != 0f) {
                NumberFormatUtils.format(tension + SharedPrefsUtils.getTensionAdjustment(activity!!))
            } else {
                NumberFormatUtils.format(tension)
            }
        }
    }

    override fun refreshViews() {
        displayTension(currentHz)
        refreshHeadSizeView()
        refreshStringDiameterView()
        refreshStringType()
        refreshCalibrated()
    }

    private fun refreshStringType() {
        stringTypeValue?.text = StringTypeUtils.stringTypesArrayList[SharedPrefsUtils.getStringType(activity!!)].name
    }

    private fun refreshStringDiameterView() {
        stringDiameterValue?.text = getString(R.string.value_space_unit,
                SharedPrefsUtils.getStringsDiameter(activity!!).toString(),
                getString(R.string.mm))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_CODE)
        } else {
            samplingManager.startSampling(activity!!, visualizerFrameLayout)
        }

        headSizeLayout.setOnClickListener {
            val dialog = HeadSizeDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, HEAD_SIZE_DIALOG_TAG)
        }

        stringDiameterLayout.setOnClickListener {
            val dialog = StringDiameterDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, STRINGS_DIAMETER_DIALOG_TAG)
        }

        stringTypeLayout.setOnClickListener {
            val dialog = StringTypeDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, STRING_TYPE_DIALOG_TAG)
        }

        refreshHeadSizeView()
        refreshStringDiameterView()
        refreshStringType()
        refreshCalibrated()

        // if first time
        if (SharedPrefsUtils.isFirstRun(activity!!)) {
            showInstructionsDialog()
        }

        displayTension(0f);
    }

    private fun showInstructionsDialog() {
        val dialog = InstructionsDialogFragment()
        dialog.setTargetFragment(this, 0)
        dialog.show(fragmentManager, INSTRUCTIONS_DIALOG_FRAGMENT_TAG)
    }

    private fun refreshCalibrated() {
        calibrationTextView.setTypeface(null, Typeface.BOLD)
//        calibrationTextView.paintFlags = calibrationTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        val units = if (SharedPrefsUtils.isTensoinImperialUnits(activity!!)) {
            getString(R.string.tension_lb)
        } else {
            getString(R.string.tension_kg)
        }

        if (SharedPrefsUtils.isCalibrated(activity!!)) {
            calibrationTextView.text = getString(R.string.personal_mode)
            val adjustment = SharedPrefsUtils.getTensionAdjustment(activity!!)
            if (adjustment != 0f) {
                val sign = if (adjustment > 0) {
                    "+"
                } else {
                    ""
                }
                personalAdjustTextView.text = getString(R.string.current_adjustment, sign, NumberFormatUtils.format(adjustment), units)
                personalAdjustTextView.visibility = View.VISIBLE
            } else {
                personalAdjustTextView.visibility = View.VISIBLE
                personalAdjustTextView.text = getString(R.string.no_calibration, units)
            }
        } else {
            calibrationTextView.text = getString(R.string.fabric_mode)
            personalAdjustTextView.visibility = View.GONE
            personalAdjustTextView.text = getString(R.string.no_calibration)
        }
    }

    private fun refreshHeadSizeView() {
        if (activity != null) {
            val headSize = SharedPrefsUtils.getRacquetHeadSize(activity!!)
            if (SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
                headSizeValue.text = getString(R.string.value_space_unit,
                        NumberFormatUtils.round(headSize),
                        getString(R.string.square_inch))
            } else {
                headSizeValue.text = getString(R.string.value_space_unit,
                        NumberFormatUtils.round(headSize),
                        getString(R.string.square_cm))
            }
        }
    }

    // TODO [musashi] add dialogs to inform user why mic is needed
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_AUDIO_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    samplingManager.startSampling(activity!!, visualizerFrameLayout)
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
