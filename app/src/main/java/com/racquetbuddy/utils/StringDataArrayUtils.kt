package com.racquetbuddy.utils

/**
 * Created by musashiwarrior on 31-Oct-18.
 */
object StringDataArrayUtils {

    const val STRING_PATTERN_DEFAULT = 4
    const val STRING_TYPE_DEFAULT = 1
    const val STRINGERS_STYLE_DEFAULT = 2
    const val GROMMET_DEFAULT = 1

    val stringTypesArrayList = arrayListOf(
            StringType("Heavy polyester", 1.38f),
            StringType("Average polyester", 1.35f),
            StringType("Light polyester", 1.32f),
            StringType("Heavy synthetic", 1.16f),
            StringType("Average synthetic", 1.12f),
            StringType("Light synthetic", 1.08f),
            StringType("Natural gut", 1.28f)
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
            Grommet("increasing the tension", 0.98f),
            Grommet("not influenced",1f),
            Grommet("decreasing the tension",1.02f)
    )
    class StringType(val name: String, val density: Float)
    class StringPattern(val name: String, val coefficient: Float)
    class StringersStyle(val name: String, val coefficient: Float)
    class Grommet(val name: String, val coefficient: Float)
}