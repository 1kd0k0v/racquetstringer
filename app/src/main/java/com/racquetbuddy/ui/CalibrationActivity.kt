package com.racquetbuddy.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.racquetbuddy.racquetstringer.R
import kotlinx.android.synthetic.main.calibration_activity.*


class CalibrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calibration_activity)

        setSupportActionBar(my_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CalibrationFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.calibration, menu)
        return true
    }

}
