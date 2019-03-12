package com.racquetbuddy.utils

import com.racquetbuddy.businesslogic.DefaultRacquetValues

/**
 * Created by musashiwarrior on 31-Oct-18.
 */
object StringTypeUtils {

    val stringTypesArrayList = arrayListOf(
            StringType("polyester", 1.35f),
            StringType("co-polyester", 1.35f),
            StringType("synthetic gut", 1.12f),
            StringType("natural gut", 1.28f)
//            StringType("hybrid - natural gut/polyester", 1.30f),
//            StringType("hybrid - synthetic gut/polyester", 1.24f),
//            StringType("hybrid - natural gut/synthetic gut", 1.21f)
//            StringType("kirschbaum, max power", 1.36f),
//            StringType("luxilon, adrenalin", 1.29f),
//            StringType("luxilon, alupower", 1.33f),
//            StringType("big hitter, tourna, silver", 1.39f),
//            StringType("ytex protour, co-poliester mix, orange", 1.39f),
//            StringType("yonex poli tour pro,  poliester", 1.31f),
//            StringType("babolat,  rpm blast", 1.38f),
//            StringType("babolat pro hurricane tour", 1.37f)
    )

    fun getDensity(name: String): Float {
        val result = stringTypesArrayList.filter { it.name == name }
        if (result.isNotEmpty()) return result[0].density
        return DefaultRacquetValues.DEFAULT_RHO
    }

    fun getDensity(id: Int): Float {
        if (id >= stringTypesArrayList.size) return stringTypesArrayList[0].density
        return stringTypesArrayList[id].density
    }

    class StringType(val name: String, val density: Float)
}