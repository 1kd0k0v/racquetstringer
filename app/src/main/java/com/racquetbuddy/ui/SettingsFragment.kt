package com.racquetbuddy.ui

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.ui.dialog.StringDiameterDialogFragment
import com.racquetbuddy.utils.SharedPrefsUtils

class SettingsFragment : PreferenceFragmentCompat(), OnRefreshViewsListener {
    override fun refreshViews() {
        if (activity != null) {
            (activity as OnRefreshViewsListener).refreshViews()
        }
        initHeadSize()
        initStringsDiameter()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHeadSize()
        initStringsDiameter()
    }

    private fun initHeadSize() {
        val keyHeadSize = findPreference("pref_key_head_size")
        keyHeadSize.summary = SharedPrefsUtils.getRacquetHeadSize(context!!).toString()
        keyHeadSize.setOnPreferenceClickListener {
            val dialog = HeadSizeDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, "HEAD_SIZE")
            return@setOnPreferenceClickListener true
        }
    }

    private fun initStringsDiameter() {
        val stringsDiameter = findPreference("pref_key_string_diameter")
        stringsDiameter.summary = SharedPrefsUtils.getStringsDiameter(context!!).toString()
        stringsDiameter.setOnPreferenceClickListener {
            val dialog = StringDiameterDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, "STRING_DIAMETER")
            return@setOnPreferenceClickListener true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}
