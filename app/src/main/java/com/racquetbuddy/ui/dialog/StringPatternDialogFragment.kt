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


class StringPatternDialogFragment : DialogFragment() {

    private var onStringTypeChangeListener: OnStringPatternChangeListener? = null

    private var defaultSelection: Int = 0

    companion object {
        fun newInstance(selection: Int, listener: OnStringPatternChangeListener): StringPatternDialogFragment {
            val dialog = StringPatternDialogFragment()
            dialog.setOnStringPatternChangeListener(listener)
            dialog.setDefaultSelection(selection)
            return dialog
        }
    }

    fun setDefaultSelection(selection: Int) {
        defaultSelection = selection
    }

    fun setOnStringPatternChangeListener(listener: OnStringPatternChangeListener) {
        this.onStringTypeChangeListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.radio_group_layout, null)

        var selected = defaultSelection

        val radioGroup = root?.findViewById<RadioGroup>(R.id.stringTypeRadioGroup)
        radioGroup?.orientation = LinearLayout.VERTICAL

        val array = StringDataArrayUtils.stringPatternArrayList

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
                .setView(root).setMessage(R.string.dialog_title_string_pattern)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    onStringTypeChangeListener?.onChange(selected)
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dismiss()
                }.create()
    }
}

interface OnStringPatternChangeListener {
    fun onChange(stringPattern: Int)
}