package no.kristiania.pgr208_1.exam

import java.lang.Double.parseDouble
import java.math.BigDecimal
import java.math.RoundingMode

fun round(number: Double, decimalAmount: Int?): Double {
    val doubleString = number.toString()
    val digitsLeftOfDot = doubleString.indexOf('.')

    if(decimalAmount == null) {
        val autoDecimalAmount = when(digitsLeftOfDot) {
            1, 2 -> 4
            else -> 2
        }
        val d = parseDouble(doubleString)
        return BigDecimal(d).setScale(autoDecimalAmount, RoundingMode.HALF_EVEN).toDouble()
    }

    return BigDecimal(number).setScale(decimalAmount, RoundingMode.HALF_EVEN).toDouble()
}

fun round(number: String, decimalAmount: Int?): Double {
    return round(number.toDouble(), decimalAmount)
}