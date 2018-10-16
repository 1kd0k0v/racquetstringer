package com.racquetstringer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.racquetstringer.audioanalyzer.SamplingLoop
import com.racquetstringer.racquetstringer.R
import com.racquetstringer.ui.dialog.HeadSizeDialogFragment
import com.racquetstringer.ui.dialog.StringDiameterDialogFragment
import com.racquetstringer.utils.NumberFormatUtils
import com.racquetstringer.utils.SharedPrefsUtils
import com.racquetstringer.utils.UnitConvertionUtils
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    val RECORD_AUDIO_CODE = 0
    val HEAD_SIZE_DIALOG_TAG = "HEAD_SIZE_DIALOG_TAG"
    val STRINGS_DIAMETER_DIALOG_TAG = "STRINGS_DIAMETER_DIALOG_TAG"

    lateinit var samplingLoop: SamplingLoop

    fun getSamplingLoopInstance(): SamplingLoop {
        return SamplingLoop(com.racquetstringer.audioanalyzer.SamplingLoop.AnalyzerCallback {
            android.util.Log.d("Amplitude", "Amp: $it");
            activity?.runOnUiThread {
                if (it > 300 && it < 800) {
                    // TODO [musashi] use selected racquet
                    val firstRacquet = com.racquetstringer.businesslogic.Racquet()

                    val displayValue = if (com.racquetstringer.utils.SharedPrefsUtils.areImperialMeasureUnits(context!!)) {
                        NumberFormatUtils.format(com.racquetstringer.utils.UnitConvertionUtils.kiloToPound(firstRacquet.getStringsTension(it))) + "lb"
                    } else {
                        NumberFormatUtils.format(firstRacquet.getStringsTension(it)) + "kg"
                    }

                    displayTensionTextView.text = displayValue
                }
            }

        }, resources)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        samplingLoop = getSamplingLoopInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_CODE)
        } else {
            // TODO [musashi] add this to onResume
            startSampling()
        }

        playPauseButton.setOnClickListener {
            if (samplingLoop.isAlive) {
                stopSampling()
            } else {
                startSampling()
            }
        }

        headSizeLayout.setOnClickListener {
            HeadSizeDialogFragment().show(fragmentManager, HEAD_SIZE_DIALOG_TAG)
        }

        stringDiameterLayout.setOnClickListener {
            val dialog = StringDiameterDialogFragment()
            dialog.show(fragmentManager, STRINGS_DIAMETER_DIALOG_TAG)
        }

        refreshHeadSizeView()

        stringDiameterValue.text = SharedPrefsUtils.getStringsDiameter(activity!!).toString() + getString(R.string.mm)
    }

    fun refreshHeadSizeView() {
        val headSize = SharedPrefsUtils.getRacquetHeadSize(activity!!)
        if (SharedPrefsUtils.areImperialMeasureUnits(activity!!)) {
            headSizeValue.text = NumberFormatUtils.round(headSize) + "in\u00B2"
        } else {
            headSizeValue.text = NumberFormatUtils.round(UnitConvertionUtils.inToCm(headSize.toDouble())) + "cm\u00B2"
        }
    }

    // TODO [musashi] create spinner like UI to show user that mic is working
    private fun startSampling() {
        samplingLoop = getSamplingLoopInstance()
        samplingLoop.start()

        playPauseButton.setText(R.string.pause)
        playPauseButton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0)
    }

    override fun onPause() {
        super.onPause()
        stopSampling()
    }

    private fun stopSampling() {
        playPauseButton.setText(R.string.play)
        playPauseButton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0)
        samplingLoop.finish()
    }


    // TODO [musashi] add dialogs to inform user why mic is needed
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_AUDIO_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startSampling()
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

        const val RACQUET_ID = "RACQUET_ID"
    }

}
