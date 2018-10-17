package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.MainFragment
import com.racquetbuddy.ui.OnRefreshViewsListener
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitConvertionUtils
import kotlin.math.roundToInt

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class HeadSizeDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.input_head_size_layout, null)

        val unitsTextView = root?.findViewById<TextView>(R.id.unitsTextView)
        val headSizeInput = root?.findViewById<EditText>(R.id.stringsDiameterInputFieldDialog)

        if (SharedPrefsUtils.areImperialMeasureUnits(activity!!)) {
            headSizeInput?.setText(SharedPrefsUtils.getRacquetHeadSize(activity!!).toString())
            headSizeInput?.setSelection(headSizeInput.text.length);
            unitsTextView?.text = getString(R.string.square_inch)
        } else {
            headSizeInput?.setText(UnitConvertionUtils.inToCm(SharedPrefsUtils.getRacquetHeadSize(activity!!).toDouble()).toString())
            headSizeInput?.setSelection(headSizeInput.text.length);
            unitsTextView?.text = getString(R.string.square_cm)
        }

        return AlertDialog.Builder(activity!!)
            .setView(root).setMessage(R.string.dialog_title_head_size)
                .setPositiveButton(R.string.save
                ) { dialog, id ->
                    if (SharedPrefsUtils.areImperialMeasureUnits(activity!!)) {
                        SharedPrefsUtils.setRacquetHeadSize(activity!!, headSizeInput?.text.toString().toDouble())
                    } else {
                        SharedPrefsUtils.setRacquetHeadSize(activity!!, UnitConvertionUtils.cmToIn(headSizeInput?.text.toString().toDouble()).toDouble().roundToInt().toDouble())
                    }

                    if (targetFragment is OnRefreshViewsListener) {
                        (targetFragment as OnRefreshViewsListener).refreshViews()
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dismiss()
                }.create()
    }
}