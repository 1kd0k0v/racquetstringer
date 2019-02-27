package com.racquetbuddy.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.racquetbuddy.racquetstringer.R
import kotlinx.android.synthetic.main.calibration_activity.*


class CalibrationActivity : AppCompatActivity() {

    companion object {
        const val RETURN_TO_SETTINGS = "RETURN_TO_SETTINGS"
        const val RETURN_TO_SETTINGS_RESULT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calibration_activity)

        setSupportActionBar(my_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        my_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CalibrationFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onBackPressed() {
        addExtraToBackToSettings()
        super.onBackPressed()
    }

    private fun addExtraToBackToSettings() {
        val bundle = Intent()
        bundle.putExtra(RETURN_TO_SETTINGS, RETURN_TO_SETTINGS_RESULT)
        setResult(Activity.RESULT_OK, bundle)
    }
}
