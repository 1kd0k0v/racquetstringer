package com.racquetbuddy.ui

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.ui.dialog.StringDiameterDialogFragment
import com.racquetbuddy.utils.SharedPrefsUtils

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
