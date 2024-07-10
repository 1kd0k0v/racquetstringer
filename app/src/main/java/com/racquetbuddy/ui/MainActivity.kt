package com.racquetbuddy.ui

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.racquetbuddy.racquetstringer.R
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPagerMain

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

        if (savedInstanceState != null) {
            viewPagerMain.currentItem = savedInstanceState.getInt("currentItem", 0)
        }
    }

    class MainPagerAdapter(context: Context, fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager) {

        private val fragmentHashMap = HashMap<Int, Fragment>()

        private val tabTitles = arrayOf(
            context.getString(R.string.measure),
            context.getString(R.string.action_settings)
        )

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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putInt("currentItem", viewPagerMain.currentItem)
        super.onSaveInstanceState(outState, outPersistentState)
    }
}
