package com.racquetbuddy.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.racquetbuddy.racquetstringer.BuildConfig
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.ui.dialog.StringDiameterDialogFragment
import com.racquetbuddy.ui.dialog.StringTypeDialogFragment
import com.racquetbuddy.ui.dialog.UnitsDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.UnitConvertionUtils

class SettingsFragment : PreferenceFragmentCompat(), OnRefreshViewsListener {
    override fun refreshViews() {
        initHeadSize()
        initStringsDiameter()
        initUnits()
        initStringType()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHeadSize()
        initStringsDiameter()
        initUnits()
        initStringType()

        val appVersion = findPreference("pref_key_app_version")
        if (appVersion != null) {
            appVersion.summary = BuildConfig.VERSION_NAME
        }

        val shareApp = findPreference("key_")
        if (shareApp != null) {
            shareApp.setOnPreferenceClickListener {
                shareTheApp()
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringType() {
        val stringType = findPreference("pref_key_string_type")
        if (stringType != null) {
            val types = activity!!.resources.getStringArray(R.array.string_types)
            stringType.summary = types[SharedPrefsUtils.getStringType(activity!!)]
        }
        stringType.setOnPreferenceClickListener {
            val dialog = StringTypeDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, "STRING_TYPE")
            return@setOnPreferenceClickListener true
        }
    }

    private fun shareTheApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + activity!!.packageName)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun initHeadSize() {
        val keyHeadSize = findPreference("pref_key_head_size")
        if (keyHeadSize != null) {

            val size = SharedPrefsUtils.getRacquetHeadSize(activity!!)
            if (SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
                keyHeadSize.summary = NumberFormatUtils.round(size) + "in\u00B2"
            } else {
                keyHeadSize.summary = NumberFormatUtils.round(size) + "cm\u00B2"
            }

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
            stringsDiameter.summary = SharedPrefsUtils.getStringsDiameter(activity!!).toString() + getString(R.string.mm)
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
            if (SharedPrefsUtils.isTensoinImperialUnits(activity!!)) {
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