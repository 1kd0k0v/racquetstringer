package com.racquetstringer.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.racquetstringer.audioanalyzer.SamplingLoop
import com.racquetstringer.businesslogic.Racquet
import com.racquetstringer.racquetstringer.R
import com.racquetstringer.utils.SharedPrefsUtils
import com.racquetstringer.utils.UnitConvertionUtils
import kotlinx.android.synthetic.main.fragment_main.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class MainFragment : Fragment() {

    val RECORD_AUDIO_CODE = 0

    var samplingLoop: SamplingLoop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_CODE)
        } else {
            // TODO [musashi] add this to onResume
            startSampling()
        }
    }

    // TODO [musashi] create spinner like UI to show user that mic is working
    private fun startSampling() {
        samplingLoop = SamplingLoop(SamplingLoop.AnalyzerCallback {
            Log.d("Amplitude", "Amp: $it");
            activity?.runOnUiThread {
                if (it > 300 && it < 800) {
                    // TODO [musashi] use selected racquet
                    val firstRacquet = Racquet()

                    val decimalFormat = DecimalFormat("#.00")
                    decimalFormat.roundingMode = RoundingMode.CEILING

                    val displayValue = if (SharedPrefsUtils.areImperialMeasureUnits(context!!)) {
                        decimalFormat.format(UnitConvertionUtils.kiloToPound(BigDecimal(firstRacquet.getStringsTension(it)))) + "lb"
                    } else {
                        decimalFormat.format(firstRacquet.getStringsTension(it)) + "kg"
                    }

                    displayTensionTextView.text = displayValue
                }
            }

        }, resources)
        samplingLoop?.start()
    }

    private fun stopSampling() {
        samplingLoop?.finish()
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
