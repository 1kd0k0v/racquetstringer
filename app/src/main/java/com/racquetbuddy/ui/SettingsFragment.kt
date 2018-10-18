package com.racquetbuddy.ui

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.racquetbuddy.racquetstringer.BuildConfig
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.ui.dialog.StringDiameterDialogFragment
import com.racquetbuddy.ui.dialog.UnitsDialogFragment
import com.racquetbuddy.utils.SharedPrefsUtils

class SettingsFragment : PreferenceFragmentCompat(), OnRefreshViewsListener {
    override fun refreshViews() {
        if (activity != null) {
            (activity as OnRefreshViewsListener).refreshViews()
        }
        initHeadSize()
        initStringsDiameter()
        initUnits()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHeadSize()
        initStringsDiameter()
        initUnits()

        val appVersion = findPreference("pref_key_app_version")
        if (appVersion != null) {
            appVersion.summary = BuildConfig.VERSION_NAME
        }
    }

    private fun initHeadSize() {
        val keyHeadSize = findPreference("pref_key_head_size")
        if (keyHeadSize != null) {
            keyHeadSize.summary = SharedPrefsUtils.getRacquetHeadSize(context!!).toString()
            keyHeadSize.setOnPreferenceClickListener {
                val dialog = HeadSizeDialogFragment()
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "HEAD_SIZE")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringsDiameter() {
        val stringsDiameter = findPreference("pref_key_string_diameter")
        if (stringsDiameter != null) {
            stringsDiameter.summary = SharedPrefsUtils.getStringsDiameter(context!!).toString()
            stringsDiameter.setOnPreferenceClickListener {
                val dialog = StringDiameterDialogFragment()
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "STRING_DIAMETER")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initUnits() {
        val units = findPreference("pref_key_units")
        if (units != null) {
            if (SharedPrefsUtils.areImperialMeasureUnits(activity!!)) {
                units.summary = getString(R.string.imperial)
            } else {
                units.summary = getString(R.string.metric)
            }
            units.setOnPreferenceClickListener {
                val dialog = UnitsDialogFragment()
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "UNITS")
                return@setOnPreferenceClickListener true
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}