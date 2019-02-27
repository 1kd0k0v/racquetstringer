package com.racquetbuddy.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.racquetbuddy.racquetstringer.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        tabLayout.setupWithViewPager(viewPagerMain)
        adapter = MainPagerAdapter(applicationContext, supportFragmentManager)
        viewPagerMain.adapter = adapter
        viewPagerMain.offscreenPageLimit = 2
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

        if (savedInstanceState != null) {
            viewPagerMain.currentItem = savedInstanceState.getInt("currentItem", 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            val haveToReturnToSettings = data?.getIntExtra(CalibrationActivity.RETURN_TO_SETTINGS, 0) == CalibrationActivity.RETURN_TO_SETTINGS_RESULT

            if (haveToReturnToSettings) {
                viewPagerMain.currentItem = 1
                ((adapter as FragmentStatePagerAdapter).getItem(1) as OnRefreshViewsListener).refreshViews()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    class MainPagerAdapter(val context: Context, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        private val fragmentHashMap = HashMap<Int, Fragment>()

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

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putInt("currentItem", viewPagerMain.currentItem)
        super.onSaveInstanceState(outState, outPersistentState)
    }
}
