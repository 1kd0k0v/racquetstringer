package com.racquetbuddy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.racquetbuddy.businesslogic.SamplingManager
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.ui.dialog.HeadSizeDialogFragment
import com.racquetbuddy.ui.dialog.OnChangeListener
import com.racquetbuddy.ui.dialog.OnStringPatternChangeListener
import com.racquetbuddy.ui.dialog.OnStringTypeChangeListener
import com.racquetbuddy.ui.dialog.OnStringersStyleChangeListener
import com.racquetbuddy.ui.dialog.PermissionDialog
import com.racquetbuddy.ui.dialog.StringOpeningSizeDialogFragment
import com.racquetbuddy.ui.dialog.StringPatternDialogFragment
import com.racquetbuddy.ui.dialog.StringThicknessChangeListener
import com.racquetbuddy.ui.dialog.StringThicknessDialogFragment
import com.racquetbuddy.ui.dialog.StringTypeDialogFragment
import com.racquetbuddy.ui.dialog.StringersStyleDialogFragment
import com.racquetbuddy.utils.NumberFormatUtils
import com.racquetbuddy.utils.RacquetTensionUtils
import com.racquetbuddy.utils.SharedPrefsUtils
import com.racquetbuddy.utils.StringDataArrayUtils
import com.racquetbuddy.utils.UnitUtils
import kotlinx.android.synthetic.main.fragment_main.cl_cross_string
import kotlinx.android.synthetic.main.fragment_main.cl_cross_string_thickness
import kotlinx.android.synthetic.main.fragment_main.cl_cross_string_type
import kotlinx.android.synthetic.main.fragment_main.cl_head_size
import kotlinx.android.synthetic.main.fragment_main.cl_string_opening_size
import kotlinx.android.synthetic.main.fragment_main.cl_string_pattern
import kotlinx.android.synthetic.main.fragment_main.cl_string_thickness
import kotlinx.android.synthetic.main.fragment_main.cl_string_type
import kotlinx.android.synthetic.main.fragment_main.cl_stringers_style
import kotlinx.android.synthetic.main.fragment_main.tv_display_tension
import kotlinx.android.synthetic.main.fragment_main.tv_label_string_type
import kotlinx.android.synthetic.main.fragment_main.tv_personal_mode_units
import kotlinx.android.synthetic.main.fragment_main.tv_string_thickness_label
import kotlinx.android.synthetic.main.fragment_main.tv_string_thickness_value
import kotlinx.android.synthetic.main.fragment_main.tv_value_cross_string_thickness
import kotlinx.android.synthetic.main.fragment_main.tv_value_cross_string_type
import kotlinx.android.synthetic.main.fragment_main.tv_value_head_size
import kotlinx.android.synthetic.main.fragment_main.tv_value_string_opening_size
import kotlinx.android.synthetic.main.fragment_main.tv_value_string_pattern
import kotlinx.android.synthetic.main.fragment_main.tv_value_string_type
import kotlinx.android.synthetic.main.fragment_main.tv_value_stringers_style
import kotlinx.android.synthetic.main.fragment_main.wv_layout


class MainFragment : Fragment(), OnRefreshViewsListener {

    private val RECORD_AUDIO_CODE = 0
    private val HEAD_SIZE_DIALOG_TAG = "HEAD_SIZE_DIALOG_TAG"
    private val STRINGS_DIAMETER_DIALOG_TAG = "STRINGS_DIAMETER_DIALOG_TAG"
    private val STRING_TYPE_DIALOG_TAG = "STRING_TYPE_DIALOG_TAG"
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
        samplingManager.startSampling(requireActivity(), wv_layout)
    }

    val clearDisplayTensionRunnable = Runnable {
        currentHz = 0f
        displayTension(0f)
    }

    private fun displayTension(hz: Float) {
        if (activity == null) return

        tv_personal_mode_units.text = UnitUtils.getUnits(requireActivity())

        val tension = RacquetTensionUtils.calculateStringTension(hz, requireContext())

        tv_display_tension.text = RacquetTensionUtils.getDisplayTension(tension, requireContext())
    }

    override fun refreshViews() {
        if (activity == null) return

        displayTension(currentHz)
        refreshHeadSizeView()
        refreshStringDiameterView()
        refreshStringType()

        refreshCrossStringType()
        refreshCrossStringThicknessView()

        refreshLabels()

        refreshStringPattern()
        refreshFrame()
        refreshStringersStyle()

        if (SharedPrefsUtils.isStringHybrid(requireContext())) {
            cl_cross_string.visibility = View.VISIBLE
        } else {
            cl_cross_string.visibility = View.GONE
        }
    }

    private fun refreshStringersStyle() {
        tv_value_stringers_style.text =
            StringDataArrayUtils.stringingTypeArrayList[SharedPrefsUtils.getStringersStyle(
                requireContext()
            )].name
    }

    private fun refreshFrame() {
        tv_value_string_opening_size.text =
            StringDataArrayUtils.stringOpeningSizeArrayList[SharedPrefsUtils.getFrame(requireContext())].shortName
    }

    private fun refreshStringPattern() {
        tv_value_string_pattern.text =
            StringDataArrayUtils.stringPatternArrayList[SharedPrefsUtils.getStringPattern(
                requireContext()
            )].name
    }

    private fun refreshLabels() {
        if (SharedPrefsUtils.isStringHybrid(requireContext())) {
            tv_label_string_type.text = getString(R.string.main_type)
            tv_string_thickness_label.text = getString(R.string.main_thickness)
        } else {
            tv_label_string_type.text = getString(R.string.string_type)
            tv_string_thickness_label.text = getString(R.string.string_thickness)
        }
    }

    private fun refreshStringType() {
        tv_value_string_type?.text = getString(
            StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getStringType(requireActivity())].shortName
        )
    }

    private fun refreshStringDiameterView() {
        tv_string_thickness_value?.text = getString(
            R.string.value_space_unit,
            SharedPrefsUtils.getStringsThickness(requireActivity()).toString(),
            getString(R.string.mm)
        )
    }

    private fun refreshCrossStringType() {
        tv_value_cross_string_type?.text = getString(
            StringDataArrayUtils.stringTypesArrayList[SharedPrefsUtils.getCrossStringType(
                requireActivity()
            )].shortName
        )
    }

    private fun refreshCrossStringThicknessView() {
        tv_value_cross_string_thickness?.text = getString(
            R.string.value_space_unit,
            SharedPrefsUtils.getCrossStringsThickness(requireActivity()).toString(),
            getString(R.string.mm)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_CODE)
        } else {
            samplingManager.startSampling(requireActivity(), wv_layout)
        }

        cl_head_size.setOnClickListener {
            val dialog = HeadSizeDialogFragment()
            dialog.setTargetFragment(this, 0)
            fragmentManager?.let { it1 -> dialog.show(it1, HEAD_SIZE_DIALOG_TAG) }
        }

        cl_string_thickness.setOnClickListener {
            val dialog =
                StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getStringsThickness(
                    requireActivity()
                ),
                    object : StringThicknessChangeListener {
                        override fun setStringThickness(thickness: Float) {
                            SharedPrefsUtils.setStringsThickness(requireActivity(), thickness)
                            refreshViews()
                        }
                    })
            dialog.setTargetFragment(this, 0)
            fragmentManager?.let { it1 -> dialog.show(it1, STRINGS_DIAMETER_DIALOG_TAG) }
        }

        cl_string_type.setOnClickListener {
            val dialog =
                StringTypeDialogFragment.newInstance(
                    SharedPrefsUtils.getStringType(requireActivity()),
                    object : OnStringTypeChangeListener {
                        override fun onStringTypeChange(stringType: Int) {
                            SharedPrefsUtils.setStringType(requireActivity(), stringType)
                            refreshViews()
                        }
                    })
            dialog.setTargetFragment(this, 0)
            fragmentManager?.let { it1 -> dialog.show(it1, STRING_TYPE_DIALOG_TAG) }
        }

        cl_cross_string_type.setOnClickListener {
            val dialog =
                StringTypeDialogFragment.newInstance(
                    SharedPrefsUtils.getCrossStringType(requireActivity()),
                    object : OnStringTypeChangeListener {
                        override fun onStringTypeChange(stringType: Int) {
                            SharedPrefsUtils.setCrossStringType(requireActivity(), stringType)
                            refreshViews()
                        }
                    })
            dialog.setTargetFragment(this, 0)
            fragmentManager?.let { it1 -> dialog.show(it1, STRING_TYPE_DIALOG_TAG) }
        }

        cl_cross_string_thickness.setOnClickListener {
            val dialog =
                StringThicknessDialogFragment.newInstance(SharedPrefsUtils.getCrossStringsThickness(
                    requireActivity()
                ),
                    object : StringThicknessChangeListener {
                        override fun setStringThickness(thickness: Float) {
                            SharedPrefsUtils.setCrossStringsThickness(requireActivity(), thickness)
                            refreshViews()
                        }
                    })
            dialog.setTargetFragment(this, 0)
            fragmentManager?.let { it1 -> dialog.show(it1, STRINGS_DIAMETER_DIALOG_TAG) }
        }

        cl_string_pattern.setOnClickListener {
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
            fragmentManager?.let { it1 -> dialog.show(it1, "STRING_PATTERN") }
        }

        cl_string_opening_size.setOnClickListener {
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
        }

        cl_stringers_style.setOnClickListener {
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
            fragmentManager?.let { it1 -> dialog.show(it1, "STRINGER_STYLE") }
        }

        refreshViews()
    }

    private fun refreshHeadSizeView() {
        if (activity != null) {
            val headSize = SharedPrefsUtils.getRacquetHeadSize(requireActivity())
            if (SharedPrefsUtils.isHeadImperialUnits(requireActivity())) {
                tv_value_head_size.text = getString(
                    R.string.value_space_unit,
                    NumberFormatUtils.round(headSize),
                    getString(R.string.square_inch)
                )
            } else {
                tv_value_head_size.text = getString(
                    R.string.value_space_unit,
                    NumberFormatUtils.round(headSize),
                    getString(R.string.square_cm)
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_AUDIO_CODE -> {
                if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                    samplingManager.startSampling(requireActivity(), wv_layout)
                } else {
                    fragmentManager?.let {
                        PermissionDialog().show(
                            it,
                            PERMISSIONS_DIALOG_FRAGMENT_TAG
                        )
                    }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
