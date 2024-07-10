package com.racquetbuddy.utils

import com.racquetbuddy.racquetstringer.R

object StringDataArrayUtils {

    const val STRING_TYPE_DEFAULT = 1

    val stringTypesArrayList = arrayListOf(
        StringType(
            R.string.heavy_polyester_short,
            R.string.heavy_polyester_long,
            1.38f
        ),
        StringType(
            R.string.average_polyester_short,
            R.string.average_polyester_short,
            1.35f
        ),
        StringType(
            R.string.light_polyester_short,
            R.string.light_polyester_long,
            1.32f
        ),
        StringType(
            R.string.multifilament_short,
            R.string.multifilament_short,
            1.16f
        ),
        StringType(
            R.string.average_synthetic_short,
            R.string.average_synthetic_short,
            1.12f
        ),
        StringType(
            R.string.light_synthetic_short,
            R.string.light_synthetic_long,
            1.08f
        ),
        StringType(
            R.string.natural_gut_short,
            R.string.natural_gut_short,
            1.28f
        )
    )

    const val STRING_PATTERN_DEFAULT = 5

    val stringPatternArrayList = arrayListOf(
        StringPattern("extremely open", 0.985f),
        StringPattern("14x16", 0.99f),
        StringPattern("16×15", 0.993f),
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
        StringersStyle("very tight", 0.85f),
        StringersStyle("tight", 0.90f),
        StringersStyle("normal", 0.95f),
        StringersStyle("loose", 1.0f),
        StringersStyle("very loose", 1.08f)
    )

    const val STRING_OPENING_SIZE_DEFAULT = 2

    val stringOpeningSizeArrayList = arrayListOf(
        Frame("XL", "XL (137 mm\u00B2 or more)", 0.97f),
        Frame("L", "L (121 ÷ 136 mm\u00B2)", 0.985f),
        Frame("M", "M (105 ÷ 120 mm\u00B2)", 1f),
        Frame("S", "S (89 ÷ 104 mm\u00B2)", 1.015f),
        Frame("XS", "XS (88 mm\u00B2 or less)", 1.03f)
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

    class StringType(val shortName: Int, val longName: Int, val density: Float)
    class StringPattern(val name: String, val coefficient: Float)
    class StringersStyle(val name: String, val coefficient: Float)
    class Frame(val shortName: String, val longName: String, val coefficient: Float)
}