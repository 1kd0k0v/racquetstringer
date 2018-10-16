package com.racquetstringer.ui

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.racquetstringer.racquetstringer.R
import com.racquetstringer.ui.dialog.HeadSizeDialogFragment
import com.racquetstringer.ui.dialog.StringDiameterDialogFragment
import com.racquetstringer.utils.SharedPrefsUtils

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keyHeadSize = findPreference("pref_key_head_size")
        keyHeadSize.summary = SharedPrefsUtils.getRacquetHeadSize(context!!).toString()
        keyHeadSize.setOnPreferenceClickListener {
            HeadSizeDialogFragment().show(fragmentManager, "HEAD_SIZE")
            return@setOnPreferenceClickListener true
        }

        val stringsDiameter = findPreference("pref_key_string_diameter")
        stringsDiameter.summary = SharedPrefsUtils.getStringsDiameter(context!!).toString()
        stringsDiameter.setOnPreferenceClickListener {
            StringDiameterDialogFragment().show(fragmentManager, "STRING_DIAMETER")
            return@setOnPreferenceClickListener true
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}
