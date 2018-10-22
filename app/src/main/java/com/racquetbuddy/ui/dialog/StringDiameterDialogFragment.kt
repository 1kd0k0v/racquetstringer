package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.OnRefreshViewsListener
import com.racquetbuddy.utils.SharedPrefsUtils

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class StringDiameterDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.input_strings_diameter_layout, null)

        val unitsTextView = root?.findViewById<TextView>(R.id.unitsTextView)
        val stringsDiameterInputFieldDialog = root?.findViewById<EditText>(R.id.stringsDiameterInputFieldDialog)
        stringsDiameterInputFieldDialog?.setText(SharedPrefsUtils.getStringsDiameter(activity!!).toString())
        stringsDiameterInputFieldDialog?.setSelection(stringsDiameterInputFieldDialog.text.length);
        unitsTextView?.text = getString(R.string.mm)

        return AlertDialog.Builder(activity!!)
                .setView(root).setMessage(R.string.string_diameter_dialog_title)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    SharedPrefsUtils.setStringsDiameter(activity!!, stringsDiameterInputFieldDialog?.text.toString().toDouble())
                    if (targetFragment is OnRefreshViewsListener) {
                        (targetFragment as OnRefreshViewsListener).refreshViews()
                    }
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