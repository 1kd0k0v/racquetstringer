package com.racquetbuddy.utils

/**
 * Created by musashiwarrior on 31-Oct-18.
 */
object StringDataArrayUtils {

    const val STRING_PATTERN_DEFAULT = 4
    const val STRING_TYPE_DEFAULT = 1
    const val STRINGERS_STYLE_DEFAULT = 2
    const val FRAME_DEFAULT = 1

    val stringTypesArrayList = arrayListOf(
            StringType("heavy polyester", 1.38f),
            StringType("average polyester", 1.35f),
            StringType("light polyester", 1.32f),
            StringType("heavy synthetic", 1.16f),
            StringType("average synthetic", 1.12f),
            StringType("light synthetic", 1.08f),
            StringType("natural gut", 1.28f)
    )

    fun getStringDensity(id: Int): Float {
        if (id >= stringTypesArrayList.size) return stringTypesArrayList[0].density
        return stringTypesArrayList[id].density
    }

    fun getStringerStyle(id: Int): Float {
        if (id >= stringerStyleArrayList.size) return stringerStyleArrayList[0].coefficient
        return stringerStyleArrayList[id].coefficient
    }

    fun getFrame(id: Int): Float {
        if (id >= framesArrayList.size) return framesArrayList[0].coefficient
        return framesArrayList[id].coefficient
    }

    fun getStringPattern(id: Int): Float {
        if (id >= stringPatternArrayList.size) return stringPatternArrayList[0].coefficient
        return stringPatternArrayList[id].coefficient
    }

    val stringPatternArrayList = arrayListOf(
            StringPattern("14x16", 1.012f),
            StringPattern("16x16", 1.009f),
            StringPattern("16x17", 1.006f),
            StringPattern("16x18", 1.003f),
            StringPattern("16x19", 1f),
            StringPattern("18x17", 1.003f),
            StringPattern("18x18", 0.999f),
            StringPattern("18x19", 0.995f),
            StringPattern("18x20", 0.99f)
    )

    val stringerStyleArrayList = arrayListOf(
        StringersStyle("very tight stringing", 0.94f),
        StringersStyle("tighter stringing", 0.97f),
        StringersStyle("normal stringing", 1f),
        StringersStyle("looser stringing", 1.03f),
        StringersStyle("very loose stringing", 1.06f)
    )

    val framesArrayList = arrayListOf(
            Frame("decreasing", 0.98f),
            Frame("not influencing",1f),
            Frame("increasing",1.02f)
    )

    class StringType(val name: String, val density: Float)
    class StringPattern(val name: String, val coefficient: Float)
    class StringersStyle(val name: String, val coefficient: Float)
    class Frame(val name: String, val coefficient: Float)
}