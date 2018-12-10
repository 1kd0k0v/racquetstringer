package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.content.Intent
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
import com.racquetbuddy.utils.UnitUtils

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class CalibrateDialogFragment : DialogFragment() {

    private var defaultMeasurment: Float = 0f
    private var isImperial: Boolean = false

    companion object {
        private const val TENSION = "TENSION"
        const val ADJUSTMENT_EXTRA = "ADJUSTMENT_EXTRA"
        const val RESULT_CODE_OK = 0
        const val RESULT_CODE_CANCEL = 1

        fun newInstance(defaultMeasurement: Float): CalibrateDialogFragment {
            val fragment = CalibrateDialogFragment()
            // Supply defaultMeasurement input as an argument.
            val args = Bundle()
            args.putFloat(TENSION, defaultMeasurement)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.calibration_ajust_layout, null)

        val unitsTextView = root.findViewById<TextView>(R.id.tensionUnitsTextView)
        val tensionInput = root.findViewById<EditText>(R.id.tensionInputFieldDialog)
        val defaultMeasurementText = root.findViewById<TextView>(R.id.defaultMeasurementText)

        defaultMeasurment = arguments?.getFloat(TENSION)!!

        unitsTextView.text = UnitUtils.getUnits(activity!!)

        defaultMeasurementText.text = getString(R.string.calibration_default_text, NumberFormatUtils.formatOneDigit(defaultMeasurment), UnitUtils.getUnits(activity!!))

//        tensionInput?.setSelection(tensionInput.text.length);

        return AlertDialog.Builder(activity!!)
            .setView(root).setMessage(R.string.dialog_adjust_title)
                .setPositiveButton(R.string.ok
                ) { _, _ ->

                    if (tensionInput?.text.toString().isEmpty()) return@setPositiveButton

                    val tension = arguments?.getFloat(TENSION)
                    val adjustment = tensionInput?.text.toString().toFloat() - tension!!

                    val intent = Intent()
                    intent.putExtra(ADJUSTMENT_EXTRA, adjustment)

                    targetFragment!!.onActivityResult(targetRequestCode, RESULT_CODE_OK, intent)

                    if (targetFragment is OnRefreshViewsListener) {
                        (targetFragment as OnRefreshViewsListener).refreshViews()
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
//                    targetFragment!!.onActivityResult(targetRequestCode, RESULT_CODE_CANCEL, Intent())
                    dismiss()
                }.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}