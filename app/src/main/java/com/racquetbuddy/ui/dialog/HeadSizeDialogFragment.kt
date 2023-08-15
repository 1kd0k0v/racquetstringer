package com.racquetbuddy.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.OnRefreshViewsListener
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils

/**
 * Created by musashiwarrior on 15-Oct-18.
 */
class HeadSizeDialogFragment : DialogFragment() {

    private val MIN_IMPERIAL = 80.0
    private val MAX_IMPERIAL = 125.0
    private val MIN_METRIC = 520.0
    private val MAX_METRIC = 810.0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)

        val root = inflater.inflate(R.layout.input_head_size_layout, null)

        val unitsSpinner = root.findViewById<Spinner>(R.id.headUnitsSpinner) as Spinner
        val headSizeInputEditText = root.findViewById<EditText>(R.id.tiet_head_size)

        headSizeInputEditText?.setText(NumberFormatUtils.formatNoTrailingZeros(SharedPrefsUtils.getRacquetHeadSize(activity!!)))
        headSizeInputEditText?.setSelection(headSizeInputEditText.text.length)

        if (SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
            unitsSpinner.setSelection(0)
        } else {
            unitsSpinner.setSelection(1)
        }

        val alert = AlertDialog.Builder(activity!!)
                .setView(root).setMessage(R.string.dialog_title_head_size)
                .setPositiveButton(R.string.ok
                ) { _, _ ->
                    val isImperial = isImperial(unitsSpinner)
                    SharedPrefsUtils.setHeadImperialUnits(activity!!, isImperial)
                    SharedPrefsUtils.setRacquetHeadSize(activity!!, headSizeInputEditText?.text.toString().toDouble())

                    if (targetFragment is OnRefreshViewsListener) {
                        (targetFragment as OnRefreshViewsListener).refreshViews()
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dismiss()
                }.create()

        headSizeInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validate(unitsSpinner, s, alert, headSizeInputEditText)
            }
        })

        unitsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                validate(unitsSpinner, headSizeInputEditText.text.toString(), alert, headSizeInputEditText)
            }
        }

        return alert
    }

    private fun validate(unitsSpinner: Spinner, s: CharSequence?, alert: AlertDialog, headSizeInputEditText: EditText) {
        val isImperial = isImperial(unitsSpinner)
        if (s == null || s.isEmpty() || !isValid(s.toString().toDouble(), isImperial)) {
            onInvalidInput(alert, isImperial, headSizeInputEditText)
        } else {
            headSizeInputEditText.error = null
            alert.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        }
    }

    private fun onInvalidInput(alert: AlertDialog, isImperial: Boolean, headSizeInput: EditText) {
        alert.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        if (isImperial) {
            headSizeInput.error = getString(R.string.head_size_error_range, MIN_IMPERIAL, MAX_IMPERIAL, getString(R.string.square_inch))
        } else {
            headSizeInput.error = getString(R.string.head_size_error_range, MIN_METRIC, MAX_METRIC, getString(R.string.square_cm))
        }
    }

    private fun isValid(size: Double, isImperial: Boolean): Boolean {
        return if (isImperial) {
            size in MIN_IMPERIAL..MAX_IMPERIAL
        } else {
            size in MIN_METRIC..MAX_METRIC
        }
    }

    private fun isImperial(spinner: Spinner) : Boolean = spinner.selectedItemId == 0L

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }
}