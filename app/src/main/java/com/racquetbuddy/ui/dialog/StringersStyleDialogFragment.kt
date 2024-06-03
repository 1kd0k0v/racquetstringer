package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.utils.StringDataArrayUtils

class StringersStyleDialogFragment : DialogFragment() {

    private var onStringersStyleChangeListener: OnStringersStyleChangeListener? = null

    private var defaultSelection: Int = 0

    companion object {
        fun newInstance(selection: Int, listener: OnStringersStyleChangeListener): StringersStyleDialogFragment {
            val dialog = StringersStyleDialogFragment()
            dialog.setListener(listener)
            dialog.setDefaultSelection(selection)
            return dialog
        }
    }

    fun setDefaultSelection(selection: Int) {
        defaultSelection = selection
    }

    fun setListener(listener: OnStringersStyleChangeListener) {
        this.onStringersStyleChangeListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.radio_group_layout, null)

        var selected = defaultSelection

        val radioGroup = root?.findViewById<RadioGroup>(R.id.stringTypeRadioGroup)
        radioGroup?.orientation = LinearLayout.VERTICAL

        val array = StringDataArrayUtils.stringingTypeArrayList

        for ((i, type) in array.withIndex()) {
            val btn = RadioButton(activity)
            btn.id = i
            btn.text = type.name
            radioGroup?.addView(btn)
        }

        radioGroup?.check(selected)

        radioGroup?.setOnCheckedChangeListener { _, id ->
            selected = id}

        return AlertDialog.Builder(requireActivity())
                .setView(root).setMessage(R.string.dialog_title_stringing_type)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    onStringersStyleChangeListener?.onChange(selected)
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dismiss()
                }.create()
    }
}

interface OnStringersStyleChangeListener {
    fun onChange(stringersStyle: Int)
}