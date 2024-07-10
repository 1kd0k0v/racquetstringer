package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.utils.StringDataArrayUtils


class StringTypeDialogFragment : DialogFragment() {

    private var onStringTypeChangeListener: OnStringTypeChangeListener? = null

    private var defaultSelection: Int = 0

    companion object {
        fun newInstance(
            selection: Int,
            listener: OnStringTypeChangeListener
        ): StringTypeDialogFragment {
            val dialog = StringTypeDialogFragment()
            dialog.setOnTypeChangeListener(listener)
            dialog.setDefaultSelection(selection)
            return dialog
        }
    }

    fun setDefaultSelection(selection: Int) {
        defaultSelection = selection
    }

    fun setOnTypeChangeListener(listener: OnStringTypeChangeListener) {
        this.onStringTypeChangeListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.radio_group_layout, null)

        var selected = defaultSelection

        val radioGroup = root?.findViewById<RadioGroup>(R.id.stringTypeRadioGroup)
        radioGroup?.orientation = LinearLayout.VERTICAL

        val array = StringDataArrayUtils.stringTypesArrayList

        for ((i, type) in array.withIndex()) {
            val btn = RadioButton(activity)
            btn.id = i
            btn.text = getString(type.longName)
            radioGroup?.addView(btn)
        }

        radioGroup?.check(selected)

        radioGroup?.setOnCheckedChangeListener { _, id ->
            selected = id
        }

        return AlertDialog.Builder(requireActivity())
            .setView(root).setMessage(R.string.dialog_title_string_type)
            .setPositiveButton(
                R.string.ok
            ) { _, _ ->
                onStringTypeChangeListener?.onStringTypeChange(selected)
            }
            .setNegativeButton(
                R.string.cancel
            ) { _, _ ->
                dismiss()
            }.create()
    }
}

interface OnStringTypeChangeListener {
    fun onStringTypeChange(stringType: Int)
}