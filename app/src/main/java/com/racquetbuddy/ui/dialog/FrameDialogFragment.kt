package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.utils.StringDataArrayUtils


/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class FrameDialogFragment : DialogFragment() {

    private var mListener: OnFrameChangeListener? = null

    private var defaultSelection: Int = 0

    companion object {
        fun newInstance(selection: Int, listener: OnFrameChangeListener): FrameDialogFragment {
            val dialog = FrameDialogFragment()
            dialog.setListener(listener)
            dialog.setDefaultSelection(selection)
            return dialog
        }
    }

    fun setDefaultSelection(selection: Int) {
        defaultSelection = selection
    }

    fun setListener(listener: OnFrameChangeListener) {
        this.mListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.radio_group_layout, null)

        var selected = defaultSelection

        val radioGroup = root?.findViewById<RadioGroup>(R.id.stringTypeRadioGroup)
        radioGroup?.orientation = LinearLayout.VERTICAL

        val array = StringDataArrayUtils.framesArrayList

        for ((i, type) in array.withIndex()) {
            val btn = RadioButton(activity)
            btn.id = i
            btn.text = type.name
            radioGroup?.addView(btn)
        }

        radioGroup?.check(selected)

        radioGroup?.setOnCheckedChangeListener { _, id ->
            selected = id}

        return AlertDialog.Builder(activity!!)
                .setView(root).setMessage(R.string.dialog_title_grommet)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    mListener?.onFrameChange(selected)
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dismiss()
                }.create()
    }
}

interface OnFrameChangeListener {
    fun onFrameChange(grommet: Int)
}