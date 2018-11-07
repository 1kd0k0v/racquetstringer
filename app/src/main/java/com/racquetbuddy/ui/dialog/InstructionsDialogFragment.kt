package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import com.racquetbuddy.racquetstringer.R

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class InstructionsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.instructions_layout, null)

        return AlertDialog.Builder(activity!!)
            .setView(root).setMessage(R.string.instructions_title)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    dismiss()
                }.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}