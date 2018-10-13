package com.racquetstringer.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.racquetstringer.racquetstringer.R
import com.racquetstringer.ui.addeditracquet.AddEditRacquetFragment

class AddEditRacquetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_racquet_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AddEditRacquetFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
