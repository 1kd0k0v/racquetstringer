package com.racquetbuddy.utils

/**
 * Created by musashiwarrior on 31-Oct-18.
 */
object StringDataArrayUtils {

    const val STRING_TYPE_DEFAULT = 1

    val stringTypesArrayList = arrayListOf(
            StringType("heavy polyester - less stretchable; higher density", 1.38f),
            StringType("average polyester", 1.35f),
            StringType("light polyester - more stretchable; lower density", 1.32f),
            StringType("heavy synthetic - less stretchable; higher density", 1.16f),
            StringType("average synthetic", 1.12f),
            StringType("light synthetic - more stretchable; lower density", 1.08f),
            StringType("natural gut", 1.28f)
    )

    const val STRING_PATTERN_DEFAULT = 5

    val stringPatternArrayList = arrayListOf(
            StringPattern("extremely open", 0.985f),
            StringPattern("14x16", 0.99f),
            StringPattern("16x16", 0.995f),
            StringPattern("16x17", 0.997f),
            StringPattern("16x18", 0.998f),
            StringPattern("16x19", 1f),
            StringPattern("16x20", 1.005f),
            StringPattern("18x17", 1.005f),
            StringPattern("18x18", 1.008f),
            StringPattern("18x19", 1.01f),
            StringPattern("18x20", 1.013f),
            StringPattern("18x21", 1.015f)
    )

    const val STRINGING_TYPE_DEFAULT = 2

    val stringingTypeArrayList = arrayListOf(
            StringersStyle("very tight", 0.94f),
            StringersStyle("tight", 0.97f),
            StringersStyle("normal", 1f),
            StringersStyle("loose", 1.03f),
            StringersStyle("very loose", 1.06f)
    )

    const val STRING_OPENING_SIZE_DEFAULT = 2

    val stringOpeningSizeArrayList = arrayListOf(
            Frame("XL (137 mm2 or more)", 0.97f),
            Frame("L (121 ÷ 136 mm2)",0.985f),
            Frame("M (105 ÷ 120 mm2)",1f),
            Frame("S (89 ÷ 104 mm2)",1.015f),
            Frame("XS (88 mm2 or less)",1.03f)
    )

    fun getStringDensity(id: Int): Float {
        if (id >= stringTypesArrayList.size) return stringTypesArrayList[0].density
        return stringTypesArrayList[id].density
    }

    fun getStringerStyle(id: Int): Float {
        if (id >= stringingTypeArrayList.size) return stringingTypeArrayList[0].coefficient
        return stringingTypeArrayList[id].coefficient
    }

    fun getStringOpeningSize(id: Int): Float {
        if (id >= stringOpeningSizeArrayList.size) return stringOpeningSizeArrayList[0].coefficient
        return stringOpeningSizeArrayList[id].coefficient
    }

    fun getStringPattern(id: Int): Float {
        if (id >= stringPatternArrayList.size) return stringPatternArrayList[0].coefficient
        return stringPatternArrayList[id].coefficient
    }

    class StringType(val name: String, val density: Float)
    class StringPattern(val name: String, val coefficient: Float)
    class StringersStyle(val name: String, val coefficient: Float)
    class Frame(val name: String, val coefficient: Float)
}