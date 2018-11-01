package com.racquetbuddy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.racquetbuddy.businesslogic.Racquet
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
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

    private var currentHz: Float = 0f

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
                if (activity != null) {
                    displayTension(amplitude)
                    currentHz = amplitude
                }
            }
        })
        samplingManager.startSampling(activity!!, visualizerFrameLayout)
    }

    private fun displayTension(hz: Float) {
        if (activity != null) {

            var headSize = SharedPrefsUtils.getRacquetHeadSize(activity!!)
            if(!SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
                headSize = UnitUtils.cmToIn(headSize).toFloat()
            }

            personalModeUnitsTextView.text = UnitUtils.getUnits(activity!!)

            val tension = Racquet.getStringsTension(hz, headSize, SharedPrefsUtils.getStringsDiameter(activity!!), SharedPrefsUtils.getStringDensity(activity!!))

            if (tension != 0.0) {
                displayTensionTextView.text = if (SharedPrefsUtils.isTensoinImperialUnits(activity!!)) {

                    if (SharedPrefsUtils.isCalibrated(activity!!)) {
                        NumberFormatUtils.format(UnitUtils.kiloToPound(tension).toFloat() + SharedPrefsUtils.getTensionAdjustment(activity!!))
                    } else {
                        NumberFormatUtils.format(UnitUtils.kiloToPound(tension))
                    }
                } else {
                    if (SharedPrefsUtils.isCalibrated(activity!!)) {
                        NumberFormatUtils.format(tension + SharedPrefsUtils.getTensionAdjustment(activity!!))
                    } else {
                        NumberFormatUtils.format(tension)
                    }
                }
                val spannable = SpannableString(displayTensionTextView.text)

                val start = displayTensionTextView.length() - 1
                val end = (displayTensionTextView).length()

                spannable.setSpan(ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(RelativeSizeSpan(0.5f), start, end, 0)

                displayTensionTextView.setText(spannable, TextView.BufferType.SPANNABLE)
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
        stringTypeValue.text = StringTypeUtils.stringTypesArrayList[SharedPrefsUtils.getStringType(activity!!)].name
    }

    private fun refreshStringDiameterView() {
        stringDiameterValue.text = getString(R.string.value_space_unit,
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
    }

    private fun refreshCalibrated() {
        calibrationTextView.setTypeface(null, Typeface.BOLD)
//        calibrationTextView.paintFlags = calibrationTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        if (SharedPrefsUtils.isCalibrated(activity!!)) {
            calibrationTextView.text = getString(R.string.personal_mode)
        } else {
            calibrationTextView.text = getString(R.string.fabric_mode)
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
