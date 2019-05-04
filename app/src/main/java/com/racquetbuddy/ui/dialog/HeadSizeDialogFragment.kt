package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.Spinner
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.OnRefreshViewsListener
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class HeadSizeDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.input_head_size_layout, null)

        val unitsSpinner = root.findViewById<Spinner>(R.id.headUnitsSpinner) as Spinner
        val headSizeInput = root.findViewById<EditText>(R.id.stringsDiameterInputFieldDialog)

        headSizeInput?.setText(NumberFormatUtils.formatNoTrailingZeros(SharedPrefsUtils.getRacquetHeadSize(activity!!)))
        headSizeInput?.setSelection(headSizeInput.text.length)

        if (SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
            unitsSpinner.setSelection(0)
        } else {
            unitsSpinner.setSelection(1)
        }

        return AlertDialog.Builder(activity!!)
            .setView(root).setMessage(R.string.dialog_title_head_size)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    val isImperial = unitsSpinner.selectedItemId == 0L
                    SharedPrefsUtils.setHeadImperialUnits(activity!!, isImperial)
                    SharedPrefsUtils.setRacquetHeadSize(activity!!, headSizeInput?.text.toString().toDouble())

                    if (targetFragment is OnRefreshViewsListener) {
                        (targetFragment as OnRefreshViewsListener).refreshViews()
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dismiss()
                }.create()
    }

    // TODO [musashi] add validation
    private fun isValid(size: Double, isImperial: Boolean): Boolean {
        return if (isImperial) {
            size in 80.0..125.0
        } else {
            size in 520.0..810.0
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }
}