package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioGroup
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
class UnitsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.units_dialog_layout, null)
        var isImperialSelected = false

        val unitsRadioGroup = root?.findViewById<RadioGroup>(R.id.unitsRadioGroup)
        unitsRadioGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.imperialRadioButton -> isImperialSelected = true
                R.id.metricRadioButton -> isImperialSelected = false
            }
        }

        if (SharedPrefsUtils.areImperialMeasureUnits(activity!!)) {
            unitsRadioGroup?.check(R.id.imperialRadioButton)
        } else {
            unitsRadioGroup?.check(R.id.metricRadioButton)
        }

        return AlertDialog.Builder(activity!!)
            .setView(root).setMessage(R.string.choose_units_of_measurement)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    SharedPrefsUtils.setImperialMeasureUnits(activity!!, isImperialSelected)

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