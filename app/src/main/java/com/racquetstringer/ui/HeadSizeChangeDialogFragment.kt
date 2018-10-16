package com.racquetstringer.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.racquetstringer.racquetstringer.R
import com.racquetstringer.utils.NumberFormatUtils
import com.racquetstringer.utils.SharedPrefsUtils
import kotlinx.android.synthetic.main.input_head_size_layout.*

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class HeadSizeChangeDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.input_head_size_layout).setMessage(R.string.dialog_title_head_size)
                    .setPositiveButton(R.string.save
                    ) { dialog, id ->
                        // TODO [musashi] save to shared preferences
                    }
                    .setNegativeButton(R.string.cancel
                    ) { _, _ ->
                        dismiss()
                    }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}