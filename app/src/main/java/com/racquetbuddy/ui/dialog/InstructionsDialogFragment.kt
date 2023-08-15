package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.racquetbuddy.racquetstringer.R

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class InstructionsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity!!)
            .setTitle(R.string.instructions_title).setMessage(R.string.instructions_body)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    dismiss()
                }.create()
    }
}