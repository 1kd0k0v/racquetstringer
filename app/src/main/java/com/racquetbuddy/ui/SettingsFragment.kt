package com.racquetbuddy.ui

import android.content.Intent
import android.os.Bundle
import androidx.preference.SwitchPreference
import androidx.preference.PreferenceFragmentCompat
import android.view.View
import androidx.preference.Preference
import com.racquetbuddy.racquetstringer.BuildConfig
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.*
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.StringDataArrayUtils

class SettingsFragment : PreferenceFragmentCompat(), OnRefreshViewsListener {

    override fun refreshViews() {
        initHeadSizePreference()
        initStringTypeSwitchPreference()
        initUnits()
        initStringsThicknessPreference()
        initStringTypePreference()
        initHybridStringPreferences()
        initFrameAndGrommets()
        initStringersStyle()
        initStringPattern()
    }

    private fun initStringPattern() {
        val stringPatternPreference = findPreference("pref_key_string_pattern") as Preference?
        if (stringPatternPreference != null) {
            stringPatternPreference.summary = StringDataArrayUtils.stringPatternArrayList[SharedPrefsUtils.getStringPattern(requireActivity())].name
            stringPatternPreference.setOnPreferenceClickListener {
                val dialog =
                        StringPatternDialogFragment.newInstance(
                                SharedPrefsUtils.getStringPattern(requireActivity()),
                                object : OnStringPatternChangeListener {
                                    override fun onChange(stringPattern: Int) {
                                        SharedPrefsUtils.setStringPattern(requireActivity(), stringPattern)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                fragmentManager?.let { dialog.show(it, "STRING_PATTERN") }
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringersStyle() {
        val stringerStylePreference = findPreference("pref_key_stringer_style") as Preference?
        if (stringerStylePreference != null) {
            stringerStylePreference.summary = StringDataArrayUtils.stringingTypeArrayList[SharedPrefsUtils.getStringersStyle(requireActivity())].name
            stringerStylePreference.setOnPreferenceClickListener {
                val dialog =
                        StringersStyleDialogFragment.newInstance(
                                SharedPrefsUtils.getStringersStyle(requireActivity()),
                                object : OnStringersStyleChangeListener {
                                    override fun onChange(stringersStyle: Int) {
                                        SharedPrefsUtils.setStringersStyle(requireActivity(), stringersStyle)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                fragmentManager?.let { dialog.show(it, "STRINGER_STYLE") }
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initFrameAndGrommets() {
        val framePreference = findPreference("pref_key_string_opening_size") as Preference?
        if (framePreference != null) {
            framePreference.summary = StringDataArrayUtils.stringOpeningSizeArrayList[SharedPrefsUtils.getFrame(requireActivity())].shortName
            framePreference.setOnPreferenceClickListener {
                val dialog =
                        StringOpeningSizeDialogFragment.newInstance(
                                SharedPrefsUtils.getFrame(requireActivity()),
                                object : OnChangeListener {
                                    override fun onChange(newValue: Int) {
                                        SharedPrefsUtils.setStringOpeningSize(requireActivity(), newValue)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                fragmentManager?.let { it1 -> dialog.show(it1, "STRING_OPENING_SIZE") }
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

        val appVersion = findPreference("pref_key_app_version") as Preference?
        if (appVersion != null) {
            appVersion.summary = BuildConfig.VERSION_NAME
        }

        (findPreference("pref_key_share") as androidx.preference.Preference?)?.setOnPreferenceClickListener {
            shareTheApp()
            return@setOnPreferenceClickListener true
        } 

        val feedback = findPreference("pref_key_feedback") as Preference?
        feedback?.setOnPreferenceClickListener {
            sendFeedback()
        }
    }

    private fun initStringTypeSwitchPreference() {
        val preference = findPreference("pref_key_string_switch_preference") as SwitchPreference?
        if (preference != null) {
            preference.isChecked = SharedPrefsUtils.isStringHybrid(requireContext())
            preference.setOnPreferenceChangeListener { _, newValue ->
                SharedPrefsUtils.setStringHybrid(requireContext(), newValue as Boolean)
                refreshViews()
                return@setOnPreferenceChangeListener true
            }
        }
    }

    private fun sendFeedback(): Boolean {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/email"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("tennistension@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(Intent.createChooser(emailIntent, "Send Feedback:"))
        return true
    }

    private fun shareTheApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + requireActivity().packageName)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun initHeadSizePreference() {
        val keyHeadSize = findPreference("pref_key_head_size") as Preference?
        if (keyHeadSize != null) {

            val size = SharedPrefsUtils.getRacquetHeadSize(requireActivity())
            if (SharedPrefsUtils.isHeadImperialUnits(requireActivity())) {
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
                fragmentManager?.let { it1 -> dialog.show(it1, "HEAD_SIZE") }
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringsThicknessPreference() {
        val stringsThicknessPreference = findPreference("pref_key_string_thickness") as Preference?
        if (stringsThicknessPreference != null) {

            if (SharedPrefsUtils.isStringHybrid(requireContext())) {
                stringsThicknessPreference.setTitle(R.string.main_thickness)
            } else {
                stringsThicknessPreference.setTitle(R.string.thickness)
            }

            stringsThicknessPreference.summary = getString(R.string.value_space_unit,
                    SharedPrefsUtils.getStringsThickness(requireActivity()).toString(),
                    getString(R.string.mm))
            stringsThicknessPreference.setOnPreferenceClickListener {
                val dialog =
                        StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getStringsThickness(requireActivity()),
                        object : StringThicknessChangeListener{
                    override fun setStringThickness(thickness: Float) {
                        SharedPrefsUtils.setStringsThickness(requireActivity(), thickness)
                        refreshViews()
                    }
                })
                dialog.setTargetFragment(this, 0)
                fragmentManager?.let { it1 -> dialog.show(it1, "STRING_THICKNESS") }
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initStringTypePreference() {
        val stringTypePreference = findPreference("pref_key_string_type") as Preference?
        if (stringTypePreference != null) {

            if (SharedPrefsUtils.isStringHybrid(requireContext())) {
                stringTypePreference.setTitle(R.string.main_type)
            } else {
                stringTypePreference.setTitle(R.string.string_type)
            }

            stringTypePreference.summary = getString(StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getStringType(requireActivity())].shortName)
            stringTypePreference.setOnPreferenceClickListener {
                val dialog =
                        StringTypeDialogFragment.newInstance(
                                SharedPrefsUtils.getStringType(requireActivity()),
                                object : OnStringTypeChangeListener {
                                    override fun onStringTypeChange(stringType: Int) {
                                        SharedPrefsUtils.setStringType(requireActivity(), stringType)
                                        refreshViews()
                                    }
                                }
                        )
                dialog.setTargetFragment(this, 0)
                fragmentManager?.let { it1 -> dialog.show(it1, "STRING_TYPE") }
                return@setOnPreferenceClickListener true
            }
        }
    }

    private fun initCrossStringsThicknessPreference() {
        val preference = findPreference("pref_key_cross_string_thickness") as Preference?
        if (preference != null) {
           if (SharedPrefsUtils.isStringHybrid(requireContext())) {
               preference.summary = getString(R.string.value_space_unit,
                       SharedPrefsUtils.getCrossStringsThickness(requireActivity()).toString(),
                       getString(R.string.mm))
               preference.setOnPreferenceClickListener {
                   val dialog =
                           StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getCrossStringsThickness(requireActivity()),
                                   object : StringThicknessChangeListener {
                       override fun setStringThickness(thickness: Float) {
                           SharedPrefsUtils.setCrossStringsThickness(requireActivity(), thickness)
                           refreshViews()
                       }
                   })
                   dialog.setTargetFragment(this, 0)
                   fragmentManager?.let { dialog.show(it, "STRING_THICKNESS") }
                   return@setOnPreferenceClickListener true
               }
               preference.isVisible = true
           } else {
               preference.isVisible = false
           }
        }
    }

    private fun initCrossStringTypePreference() {
        val stringTypePreference = findPreference("pref_key_cross_string_type") as Preference?
        if (stringTypePreference != null) {
            if (SharedPrefsUtils.isStringHybrid(requireContext())) {
                stringTypePreference.summary = getString(StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getCrossStringType(requireActivity())].shortName)
                stringTypePreference.setOnPreferenceClickListener {
                    val dialog =
                            StringTypeDialogFragment.newInstance(SharedPrefsUtils.getCrossStringType(requireActivity()),
                            object : OnStringTypeChangeListener {
                        override fun onStringTypeChange(stringType: Int) {
                            SharedPrefsUtils.setCrossStringType(requireActivity(), stringType)
                            refreshViews()
                        }
                    })
                    dialog.setTargetFragment(this, 0)
                    fragmentManager?.let { it1 -> dialog.show(it1, "STRING_TYPE") }
                    return@setOnPreferenceClickListener true
                }
                stringTypePreference.isVisible = true
            } else {
                stringTypePreference.isVisible = false
            }
        }
    }

    private fun initUnits() {
        val units = findPreference("pref_key_units") as Preference?
        if (units != null) {
            if (SharedPrefsUtils.isTensoinImperialUnits(requireActivity())) {
                units.summary = getString(R.string.tension_lb)
            } else {
                units.summary = getString(R.string.tension_kg)
            }
            units.setOnPreferenceClickListener {
                val dialog = UnitsDialogFragment()
                dialog.setTargetFragment(this, 0)
                fragmentManager?.let { it1 -> dialog.show(it1, "UNITS") }
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