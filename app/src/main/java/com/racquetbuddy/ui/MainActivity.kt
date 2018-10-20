package com.racquetbuddy.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.racquetbuddy.racquetstringer.R
import com.racquetbuddy.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout.setupWithViewPager(viewPagerMain)
        adapter = MainPagerAdapter(applicationContext, supportFragmentManager)
        viewPagerMain.adapter = adapter
        viewPagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                when (p0) {
                    0 -> {
                        ((adapter as FragmentStatePagerAdapter).getItem(0) as OnRefreshViewsListener).refreshViews()
                    }

                    1 -> {
                        ((adapter as FragmentStatePagerAdapter).getItem(1) as OnRefreshViewsListener).refreshViews()
                    }
                }
            }
        })
//        if (SharedPrefsUtils.isFirstRun(this)) {
//            SharedPrefsUtils.setFirstRun(this, false)
//            setDefaultSharedPreferences()
//        }
    }

//    private fun setDefaultSharedPreferences() {
//    }

    class MainPagerAdapter(val context: Context, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        val fragmentHashMap = HashMap<Int, Fragment>()

        private val tabTitles = arrayOf(context.getString(R.string.measure),
                context.getString(R.string.action_settings))

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    val fragment = fragmentHashMap[0]
                    if (fragment == null) {
                        fragmentHashMap[0] = MainFragment.newInstance()
                    }
                    return fragmentHashMap[0]!!
                }

                1 -> {
                    val fragment = fragmentHashMap[1]
                    if (fragment == null) {
                        fragmentHashMap[1] = SettingsFragment.newInstance()
                    }
                    return fragmentHashMap[1]!!
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
