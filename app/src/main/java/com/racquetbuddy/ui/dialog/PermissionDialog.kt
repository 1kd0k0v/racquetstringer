package com.racquetbuddy.ui.dialog

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.racquetbuddy.racquetstringer.R

class PermissionDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.permission_dialog_title)
            .setMessage(R.string.permission_dialog_message)
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                ActivityCompat.requestPermissions(
                    activity as FragmentActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_AUDIO_CODE
                )
                dismiss()
            }
            .setNegativeButton(R.string.no) { _, _ ->
                activity?.finish()
            }
            .create()
    }

    companion object {
        private const val RECORD_AUDIO_CODE = 0
    }
}