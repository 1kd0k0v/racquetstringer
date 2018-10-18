package com.racquetbuddy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.racquetbuddy.audioanalyzer.SamplingLoop
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.ui.dialog.StringDiameterDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitConvertionUtils
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), OnRefreshViewsListener {

    val RECORD_AUDIO_CODE = 0
    val HEAD_SIZE_DIALOG_TAG = "HEAD_SIZE_DIALOG_TAG"
    val STRINGS_DIAMETER_DIALOG_TAG = "STRINGS_DIAMETER_DIALOG_TAG"

    lateinit var samplingLoop: SamplingLoop

    private var currentHz: Double = 0.0

    fun getSamplingLoopInstance(): SamplingLoop {

        val ampBuffer = arrayListOf<Double>()

        return SamplingLoop(com.racquetbuddy.audioanalyzer.SamplingLoop.AnalyzerCallback { amplitude ->
            android.util.Log.d("Amplitude", "Amp: $amplitude");

            if (amplitude > 300 && amplitude < 800) {
                val found = ampBuffer.find { it > amplitude - 2 && it < amplitude + 2 }
                if (found != null) {
                    activity?.runOnUiThread {
                        val avgAmplitude = (found + amplitude) / 2
                        displayTension(avgAmplitude)
                        currentHz = avgAmplitude
                    }
                    ampBuffer.clear()
                }

                if (ampBuffer.size == 1000) {
                    ampBuffer.clear()
                }

                ampBuffer.add(amplitude)
            }


        }, resources)
    }

    private fun displayTension(hz: Double) {
        val racquet = com.racquetbuddy.businesslogic.Racquet()
        var displayValue = ""
        val tension = racquet.getStringsTension(hz, SharedPrefsUtils.getRacquetHeadSize(activity!!).toDouble(), SharedPrefsUtils.getStringsDiameter(activity!!).toDouble())
        if (com.racquetbuddy.utils.SharedPrefsUtils.areImperialMeasureUnits(context!!)) {
            displayValue = NumberFormatUtils.format(com.racquetbuddy.utils.UnitConvertionUtils.kiloToPound(tension))
            unitsTensionTextVIew.text = "lb"
        } else {
            displayValue = NumberFormatUtils.format(tension)
            unitsTensionTextVIew.text = "kg"
        }
        displayTensionTextView.text = displayValue
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        samplingLoop = getSamplingLoopInstance()
    }

    override fun refreshViews() {
        displayTension(currentHz)
        refreshHeadSizeView()
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
            val dialog = HeadSizeDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, HEAD_SIZE_DIALOG_TAG)
        }

        stringDiameterLayout.setOnClickListener {
            val dialog = StringDiameterDialogFragment()
            dialog.setTargetFragment(this, 0)
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

    override fun onPause() {
        super.onPause()
        stopSampling()
    }

    // TODO [musashi] create spinner like UI to show user that mic is working
    private fun startSampling() {
        samplingLoop = getSamplingLoopInstance()
        samplingLoop.start()

        playPauseButton.setBackgroundResource(R.drawable.pause_background)
        playPauseButton.setText(R.string.pause)
        playPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_white_24dp, 0, 0, 0)
    }

    private fun stopSampling() {
        samplingLoop.finish()

        playPauseButton.setBackgroundResource(R.drawable.play_background)
        playPauseButton.setText(R.string.play)
        playPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_white_24dp, 0, 0, 0)
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
