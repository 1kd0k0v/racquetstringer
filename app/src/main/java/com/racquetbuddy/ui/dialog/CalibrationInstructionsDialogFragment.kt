package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.racquetbuddy.racquetstringer.R

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class CalibrationInstructionsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity!!)
            .setMessage(R.string.calibration_instructions_body).setTitle(R.string.calibration_instructions_title)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    dismiss()
                }.create()
    }
}