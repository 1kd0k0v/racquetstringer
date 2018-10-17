package com.racquetbuddy.utils

/**
 * Created by musashiwarrior on 17-Oct-18.
 */
object FragmentUtils {
    fun makeFragmentName(viewId: Int, id: Long): String {
        return "android:switcher:$viewId:$id"
    }
}