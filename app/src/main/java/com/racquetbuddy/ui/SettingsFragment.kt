package com.racquetbuddy.ui

import android.content.Intent
import android.os.Bundle
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.racquetbuddy.racquetstringer.BuildConfig
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.*
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.StringDataArrayUtils

class SettingsFragment : PreferenceFragmentCompat(), OnRefreshViewsListener {

    private val CHOOSE_MODE_CODE = 0

    override fun refreshViews() {
        initHeadSizePreference()
        initStringTypeSwitchPreference()
        initUnits()
        initMode()
        initStringsThicknessPreference()
        initStringTypePreference()
        initHybridStringPreferences()
        initFrameAndGrommets()
        initStringersStyle()
        initStringPattern()
    }

    private fun initStringPattern() {
        val stringPatternPreference = findPreference("pref_key_string_pattern")
        if (stringPatternPreference != null) {
            stringPatternPreference.summary = StringDataArrayUtils.stringPatternArrayList[SharedPrefsUtils.getStringPattern(activity!!)].name
            stringPatternPreference.setOnPreferenceClickListener {
                val dialog =
                        StringPatternDialogFragment.newInstance(
                                SharedPrefsUtils.getStringPattern(activity!!),
                                object : OnStringPatternChangeListener {
                                    override fun onChange(stringPattern: Int) {
                                        SharedPrefsUtils.setStringPattern(activity!!, stringPattern)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "STRING_PATTERN")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringersStyle() {
        val stringerStylePreference = findPreference("pref_key_stringer_style")
        if (stringerStylePreference != null) {
            stringerStylePreference.summary = StringDataArrayUtils.stringingTypeArrayList[SharedPrefsUtils.getStringersStyle(activity!!)].name
            stringerStylePreference.setOnPreferenceClickListener {
                val dialog =
                        StringersStyleDialogFragment.newInstance(
                                SharedPrefsUtils.getStringersStyle(activity!!),
                                object : OnStringersStyleChangeListener {
                                    override fun onChange(stringersStyle: Int) {
                                        SharedPrefsUtils.setStringersStyle(activity!!, stringersStyle)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "STRINGER_STYLE")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initFrameAndGrommets() {
        val framePreference = findPreference("pref_key_string_opening_size")
        if (framePreference != null) {
            framePreference.summary = StringDataArrayUtils.stringOpeningSizeArrayList[SharedPrefsUtils.getFrame(activity!!)].shortName
            framePreference.setOnPreferenceClickListener {
                val dialog =
                        StringOpeningSizeDialogFragment.newInstance(
                                SharedPrefsUtils.getFrame(activity!!),
                                object : OnChangeListener {
                                    override fun onChange(newValue: Int) {
                                        SharedPrefsUtils.setStringOpeningSize(activity!!, newValue)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "STRING_OPENING_SIZE")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initHybridStringPreferences() {
        initCrossStringTypePreference()
        initCrossStringsThicknessPreference()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshViews()

        val appVersion = findPreference("pref_key_app_version")
        if (appVersion != null) {
            appVersion.summary = BuildConfig.VERSION_NAME
        }

        findPreference("pref_key_share")?.setOnPreferenceClickListener {
            shareTheApp()
            return@setOnPreferenceClickListener true
        }

        val feedback = findPreference("pref_key_feedback")
        feedback?.setOnPreferenceClickListener {
            sendFeedback()
        }

        val instructions = findPreference("pref_key_instructions")
        instructions?.setOnPreferenceClickListener {
            val dialog = InstructionsDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, "INSTRUCTIONS_TYPE")
            return@setOnPreferenceClickListener true
        }
    }

    private fun initStringTypeSwitchPreference() {
        val preference = findPreference("pref_key_string_switch_preference") as SwitchPreference?
        if (preference != null) {
            preference.isChecked = SharedPrefsUtils.isStringHybrid(context!!)
            preference.setOnPreferenceChangeListener { _, newValue ->
                SharedPrefsUtils.setStringHybrid(context!!, newValue as Boolean)
                refreshViews()
                return@setOnPreferenceChangeListener true
            }
        }
    }

    private fun sendFeedback(): Boolean {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/email"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ikdokov@gmail.me"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(Intent.createChooser(emailIntent, "Send Feedback:"))
        return true
    }

    private fun initMode() {
        val calibration = findPreference("pref_key_own_calibration")
        if (calibration != null) {
            if (SharedPrefsUtils.isCalibrated(activity!!)) {
                calibration.summary = getString(R.string.on)
            } else {
                calibration.summary = getString(R.string.off)
            }

            calibration.setOnPreferenceClickListener {
                startActivityForResult(Intent(context, CalibrationActivity::class.java), CHOOSE_MODE_CODE)
                return@setOnPreferenceClickListener true
            }
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

    private fun initHeadSizePreference() {
        val keyHeadSize = findPreference("pref_key_head_size")
        if (keyHeadSize != null) {

            val size = SharedPrefsUtils.getRacquetHeadSize(activity!!)
            if (SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
                keyHeadSize.summary = getString(R.string.value_space_unit,
                        NumberFormatUtils.round(size),
                        getString(R.string.square_inch))
            } else {
                keyHeadSize.summary = getString(R.string.value_space_unit,
                        NumberFormatUtils.round(size),
                        getString(R.string.square_cm))
            }

            keyHeadSize.setOnPreferenceClickListener {
                val dialog = HeadSizeDialogFragment()
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "HEAD_SIZE")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringsThicknessPreference() {
        val stringsThicknessPreference = findPreference("pref_key_string_thickness")
        if (stringsThicknessPreference != null) {

            if (SharedPrefsUtils.isStringHybrid(context!!)) {
                stringsThicknessPreference.setTitle(R.string.main_thickness)
            } else {
                stringsThicknessPreference.setTitle(R.string.thickness)
            }

            stringsThicknessPreference.summary = getString(R.string.value_space_unit,
                    SharedPrefsUtils.getStringsThickness(activity!!).toString(),
                    getString(R.string.mm))
            stringsThicknessPreference.setOnPreferenceClickListener {
                val dialog =
                        StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getStringsThickness(activity!!),
                        object : StringThicknessChangeListener{
                    override fun setStringThickness(thickness: Float) {
                        SharedPrefsUtils.setStringsThickness(activity!!, thickness)
                        refreshViews()
                    }
                })
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "STRING_THICKNESS")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringTypePreference() {
        val stringTypePreference = findPreference("pref_key_string_type")
        if (stringTypePreference != null) {

            if (SharedPrefsUtils.isStringHybrid(context!!)) {
                stringTypePreference.setTitle(R.string.main_type)
            } else {
                stringTypePreference.setTitle(R.string.string_type)
            }

            stringTypePreference.summary = getString(StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getStringType(activity!!)].shortName)
            stringTypePreference.setOnPreferenceClickListener {
                val dialog =
                        StringTypeDialogFragment.newInstance(
                                SharedPrefsUtils.getStringType(activity!!),
                                object : OnStringTypeChangeListener {
                                    override fun onStringTypeChange(stringType: Int) {
                                        SharedPrefsUtils.setStringType(activity!!, stringType)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "STRING_TYPE")
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initCrossStringsThicknessPreference() {
        val preference = findPreference("pref_key_cross_string_thickness")
        if (preference != null) {
           if (SharedPrefsUtils.isStringHybrid(context!!)) {
               preference.summary = getString(R.string.value_space_unit,
                       SharedPrefsUtils.getCrossStringsThickness(activity!!).toString(),
                       getString(R.string.mm))
               preference.setOnPreferenceClickListener {
                   val dialog =
                           StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getCrossStringsThickness(activity!!),
                                   object : StringThicknessChangeListener {
                       override fun setStringThickness(thickness: Float) {
                           SharedPrefsUtils.setCrossStringsThickness(activity!!, thickness)
                           refreshViews()
                       }
                   })
                   dialog.setTargetFragment(this, 0)
                   dialog.show(fragmentManager, "STRING_THICKNESS")
                   return@setOnPreferenceClickListener true
               }
               preference.isVisible = true
           } else {
               preference.isVisible = false
           }
        }
    }

    private fun initCrossStringTypePreference() {
        val stringTypePreference = findPreference("pref_key_cross_string_type")
        if (stringTypePreference != null) {
            if (SharedPrefsUtils.isStringHybrid(context!!)) {
                stringTypePreference.summary = getString(StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getCrossStringType(activity!!)].shortName)
                stringTypePreference.setOnPreferenceClickListener {
                    val dialog =
                            StringTypeDialogFragment.newInstance(SharedPrefsUtils.getCrossStringType(activity!!),
                            object : OnStringTypeChangeListener {
                        override fun onStringTypeChange(stringType: Int) {
                            SharedPrefsUtils.setCrossStringType(activity!!, stringType)
                            refreshViews()
                        }
                    })
                    dialog.setTargetFragment(this, 0)
                    dialog.show(fragmentManager, "STRING_TYPE")
                    return@setOnPreferenceClickListener true
                }
                stringTypePreference.isVisible = true
            } else {
                stringTypePreference.isVisible = false
            }
        }
    }

    private fun initUnits() {
        val units = findPreference("pref_key_units")
        if (units != null) {
            if (SharedPrefsUtils.isTensoinImperialUnits(activity!!)) {
                units.summary = getString(R.string.tension_lb)
            } else {
                units.summary = getString(R.string.tension_kg)
            }
            units.setOnPreferenceClickListener {
                val dialog = UnitsDialogFragment()
                dialog.setTargetFragment(this, 0)
                dialog.show(fragmentManager, "UNITS")
                return@setOnPreferenceClickListener true
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            refreshViews()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}