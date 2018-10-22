package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.RadioGroup
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.OnRefreshViewsListener
import com.racquetbuddy.utils.SharedPrefsUtils
import android.widget.LinearLayout



/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class StringTypeDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.input_string_type_layout, null)

        var selected = SharedPrefsUtils.getStringType(activity!!)

        val radioGroup = root?.findViewById<RadioGroup>(R.id.stringTypeRadioGroup)
        radioGroup?.orientation = LinearLayout.VERTICAL

        val array = activity!!.resources.getStringArray(R.array.string_types)

        for ((i, type) in array.withIndex()) {
            val btn = RadioButton(activity)
            btn.id = i
            btn.text = type
            radioGroup?.addView(btn)
        }

        radioGroup?.check(selected)

        radioGroup?.setOnCheckedChangeListener { _, id -> selected = id }

        return AlertDialog.Builder(activity!!)
                .setView(root).setMessage(R.string.dialog_title_string_type)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    SharedPrefsUtils.setStringType(activity!!, selected)

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