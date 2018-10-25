package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.OnRefreshViewsListener
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitConvertionUtils
import java.math.BigDecimal

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class AdjustDialogFragment : DialogFragment() {

    private var tension: Float = 0f
    private var isImperial: Boolean = false

    companion object {
        private const val TENSION = "TENSION"

        fun newInstance(num: Float): AdjustDialogFragment {
            val f = AdjustDialogFragment()
            // Supply num input as an argument.
            val args = Bundle()
            args.putFloat(TENSION, num)
            f.arguments = args
            return f
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.calibration_ajust_layout, null)

        val unitsTextView = root.findViewById<TextView>(R.id.tensionUnitsTextView)
        val tensionInput = root.findViewById<EditText>(R.id.tensionInputFieldDialog)
        val defaultMeasurementText = root.findViewById<TextView>(R.id.defaultMeasurementText)

        tension = arguments?.getFloat(TENSION)!!

        isImperial = SharedPrefsUtils.isTensoinImperialUnits(activity!!)

        if (isImperial) {
            unitsTextView.text = getString(R.string.tension_lb)
            defaultMeasurementText.text = getString(R.string.calibration_default_text, NumberFormatUtils.format(UnitConvertionUtils.kiloToPound(tension.toDouble())), getString(R.string.tension_lb))
        } else {
            unitsTextView.text = getString(R.string.tension_kg)
            defaultMeasurementText.text = getString(R.string.calibration_default_text, NumberFormatUtils.format(tension), getString(R.string.tension_kg))
        }

//        tensionInput?.setSelection(tensionInput.text.length);

        return AlertDialog.Builder(activity!!)
            .setView(root).setMessage(R.string.dialog_adjust_title)
                .setPositiveButton(R.string.ok
                ) { _, _ ->

                    val tension = arguments?.getFloat(TENSION)
                    if (isImperial) {
                        SharedPrefsUtils.setTensionAdjustmentKg(activity!!,UnitConvertionUtils.kiloToPound(tensionInput?.text.toString().toDouble()).toFloat() - tension!!)
                    } else {
                        SharedPrefsUtils.setTensionAdjustmentKg(activity!!, tensionInput?.text.toString().toFloat() - tension!!)
                    }

                    SharedPrefsUtils.setCalibrated(activity!!, true)

                    if (targetFragment is OnRefreshViewsListener) {
                        (targetFragment as OnRefreshViewsListener).refreshViews()
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dismiss()
                }.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}