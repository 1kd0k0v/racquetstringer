package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.racquetbuddy.racquetstringer.R

class StringThicknessDialogFragment : DialogFragment() {

    private var listener: StringThicknessChangeListener? = null

    private var thickness: Float = 0f

    companion object {
        fun newInstance(thickness: Float, listener: StringThicknessChangeListener) : StringThicknessDialogFragment {
            val dialog = StringThicknessDialogFragment()
            dialog.setOnThicknessChangeListener(listener)
            dialog.setThickness(thickness)
            return dialog
        }
    }

    private fun setThickness(thickness: Float) {
        this.thickness = thickness
    }

    private fun setOnThicknessChangeListener(listener: StringThicknessChangeListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.input_strings_diameter_layout, null)

        val unitsTextView = root?.findViewById<TextView>(R.id.unitsTextView)
        val stringsDiameterInputFieldDialog = root?.findViewById<EditText>(R.id.tiet_head_size)
        stringsDiameterInputFieldDialog?.setText(thickness.toString())
        stringsDiameterInputFieldDialog?.setSelection(stringsDiameterInputFieldDialog.text.length)
        unitsTextView?.text = getString(R.string.mm)

        return AlertDialog.Builder(activity!!)
                .setView(root).setMessage(R.string.string_thickness_dialog_title)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    listener?.setStringThickness(stringsDiameterInputFieldDialog?.text.toString().toFloat())
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

interface StringThicknessChangeListener {
    fun setStringThickness(thickness: Float)
}