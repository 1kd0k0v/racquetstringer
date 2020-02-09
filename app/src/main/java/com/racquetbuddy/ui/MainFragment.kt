package com.racquetbuddy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.*
import com.racquetbuddy.utils.*
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment(), OnRefreshViewsListener {

    private val RECORD_AUDIO_CODE = 0
    private val HEAD_SIZE_DIALOG_TAG = "HEAD_SIZE_DIALOG_TAG"
    private val STRINGS_DIAMETER_DIALOG_TAG = "STRINGS_DIAMETER_DIALOG_TAG"
    private val STRING_TYPE_DIALOG_TAG = "STRING_TYPE_DIALOG_TAG"
    private val INSTRUCTIONS_DIALOG_FRAGMENT_TAG = "INSTRUCTIONS_DIALOG_FRAGMENT_TAG"
    private val PERMISSIONS_DIALOG_FRAGMENT_TAG = "PERMISSIONS_DIALOG_FRAGMENT_TAG"

    private var currentHz: Float = 0f

    private val handler: Handler = Handler();

    private val samplingManager = SamplingManager.instance

    override fun onPause() {
        super.onPause()
        samplingManager.stopSampling()
    }

    override fun onResume() {
        super.onResume()
        startSampling()
    }

    private fun startSampling() {
        samplingManager.addFrequencyListener(object : SamplingManager.FrequencyListener {
            override fun getFrequency(hz: Float) {
                if (activity == null) return

                handler.removeCallbacks(clearDisplayTensionRunnable)
                displayTension(hz)
                handler.postDelayed(clearDisplayTensionRunnable, 1000)
                currentHz = hz
            }
        })
        samplingManager.startSampling(activity!!, wv_layout)
    }

    val clearDisplayTensionRunnable = Runnable {
        currentHz = 0f
        displayTension(0f)
    }

    private fun displayTension(hz: Float) {
        if (activity == null) return

        tv_personal_mode_units.text = UnitUtils.getUnits(activity!!)

        val tension = RacquetTensionUtils.calculateStringTension(hz, context!!)

        tv_display_tension.text = RacquetTensionUtils.getDisplayTension(tension, context!!)
    }

    override fun refreshViews() {
        if (activity == null) return

        displayTension(currentHz)
        refreshHeadSizeView()
        refreshStringDiameterView()
        refreshStringType()
        refreshCalibrated()

        refreshCrossStringType()
        refreshCrossStringThicknessView()

        refreshLabels()

        refreshStringPattern()
        refreshFrame()
        refreshStringersStyle()

        if (SharedPrefsUtils.isStringHybrid(context!!)) {
            cl_cross_string.visibility = View.VISIBLE
        } else {
            cl_cross_string.visibility = View.GONE
        }
    }

    private fun refreshStringersStyle() {
        tv_value_stringers_style.text = StringDataArrayUtils.stringingTypeArrayList[SharedPrefsUtils.getStringersStyle(context!!)].name
    }

    private fun refreshFrame() {
        tv_value_string_opening_size.text = StringDataArrayUtils.stringOpeningSizeArrayList[SharedPrefsUtils.getFrame(context!!)].shortName
    }

    private fun refreshStringPattern() {
        tv_value_string_pattern.text = StringDataArrayUtils.stringPatternArrayList[SharedPrefsUtils.getStringPattern(context!!)].name
    }

    private fun refreshLabels() {
        if (SharedPrefsUtils.isStringHybrid(context!!)) {
            tv_label_string_type.text = getString(R.string.main_type)
            tv_string_thickness_label.text = getString(R.string.main_thickness)
        } else {
            tv_label_string_type.text = getString(R.string.string_type)
            tv_string_thickness_label.text = getString(R.string.string_thickness)
        }
    }

    private fun refreshStringType() {
        tv_value_string_type?.text = getString(StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getStringType(activity!!)].shortName)
    }

    private fun refreshStringDiameterView() {
        tv_string_thickness_value?.text = getString(R.string.value_space_unit,
                SharedPrefsUtils.getStringsThickness(activity!!).toString(),
                getString(R.string.mm))
    }

    private fun refreshCrossStringType() {
        tv_value_cross_string_type?.text = getString(StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getCrossStringType(activity!!)].shortName)
    }

    private fun refreshCrossStringThicknessView() {
        tv_value_cross_string_thickness?.text = getString(R.string.value_space_unit,
                SharedPrefsUtils.getCrossStringsThickness(activity!!).toString(),
                getString(R.string.mm))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_CODE)
        } else {
            samplingManager.startSampling(activity!!, wv_layout)
        }

        cl_head_size.setOnClickListener {
            val dialog = HeadSizeDialogFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, HEAD_SIZE_DIALOG_TAG)
        }

        cl_string_thickness.setOnClickListener {
            val dialog =
                    StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getStringsThickness(activity!!),
                            object : StringThicknessChangeListener{
                                override fun setStringThickness(thickness: Float) {
                                    SharedPrefsUtils.setStringsThickness(activity!!, thickness)
                                    refreshViews()
                                }
                            })
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, STRINGS_DIAMETER_DIALOG_TAG)
        }

        cl_string_type.setOnClickListener {
            val dialog =
                    StringTypeDialogFragment.newInstance(
                            SharedPrefsUtils.getStringType(activity!!),
                            object : OnStringTypeChangeListener {
                                override fun onStringTypeChange(stringType: Int) {
                                    SharedPrefsUtils.setStringType(activity!!, stringType)
                                    refreshViews()
                                }
                            })
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, STRING_TYPE_DIALOG_TAG)
        }

        cl_cross_string_type.setOnClickListener {
            val dialog =
                    StringTypeDialogFragment.newInstance(
                            SharedPrefsUtils.getCrossStringType(activity!!),
                            object : OnStringTypeChangeListener {
                                override fun onStringTypeChange(stringType: Int) {
                                    SharedPrefsUtils.setCrossStringType(activity!!, stringType)
                                    refreshViews()
                                }
                            })
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, STRING_TYPE_DIALOG_TAG)
        }

        cl_cross_string_thickness.setOnClickListener {
            val dialog =
                    StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getCrossStringsThickness(activity!!),
                            object : StringThicknessChangeListener{
                                override fun setStringThickness(thickness: Float) {
                                    SharedPrefsUtils.setCrossStringsThickness(activity!!, thickness)
                                    refreshViews()
                                }
                            })
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager, STRINGS_DIAMETER_DIALOG_TAG)
        }

        cl_string_pattern.setOnClickListener {
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
        }

        cl_string_opening_size.setOnClickListener {
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
        }

        cl_stringers_style.setOnClickListener {
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
        }

        // if first time
        if (SharedPrefsUtils.isFirstRun(activity!!)) {
            showInstructionsDialog()
        }

        refreshViews()
    }

    private fun showInstructionsDialog() {
        val dialog = InstructionsDialogFragment()
        dialog.setTargetFragment(this, 0)
        dialog.show(fragmentManager, INSTRUCTIONS_DIALOG_FRAGMENT_TAG)
    }

    private fun refreshCalibrated() {
        tv_calibration?.setTypeface(null, Typeface.BOLD)

        if (SharedPrefsUtils.isCalibrated(activity!!) && SharedPrefsUtils.getTensionAdjustment(activity!!) != 0f) {
            tv_calibration.visibility = View.VISIBLE
        } else {
            tv_calibration.visibility = View.GONE
        }
    }

    private fun refreshHeadSizeView() {
        if (activity != null) {
            val headSize = SharedPrefsUtils.getRacquetHeadSize(activity!!)
            if (SharedPrefsUtils.isHeadImperialUnits(activity!!)) {
                tv_value_head_size.text = getString(R.string.value_space_unit,
                        NumberFormatUtils.round(headSize),
                        getString(R.string.square_inch))
            } else {
                tv_value_head_size.text = getString(R.string.value_space_unit,
                        NumberFormatUtils.round(headSize),
                        getString(R.string.square_cm))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_AUDIO_CODE -> {
                if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                    samplingManager.startSampling(activity!!, wv_layout)
                } else {
                    PermissionDialog().show(fragmentManager, PERMISSIONS_DIALOG_FRAGMENT_TAG)
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            refreshViews();
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
