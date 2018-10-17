package com.racquetbuddy.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnRefreshViewsListener {
    override fun refreshViews() {
        val fragmentTag = FragmentUtils.makeFragmentName(viewPagerMain.id, 0)
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment is OnRefreshViewsListener) {
            (fragment as OnRefreshViewsListener).refreshViews()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout.setupWithViewPager(viewPagerMain)
        viewPagerMain.adapter = MainPagerAdapter(applicationContext, supportFragmentManager)


//        if (SharedPrefsUtils.isFirstRun(this)) {
//            SharedPrefsUtils.setFirstRun(this, false)
//            setDefaultSharedPreferences()
//        }
    }

//    private fun setDefaultSharedPreferences() {
//    }

    class MainPagerAdapter(val context: Context, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        private val tabTitles = arrayOf(context.getString(R.string.measure),
                context.getString(R.string.action_settings))

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    return MainFragment.newInstance()
                }

                1 -> {
                    return SettingsFragment.newInstance()
                }
            }
            return MainFragment.newInstance()

        }

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabTitles[position]
        }
    }
}
