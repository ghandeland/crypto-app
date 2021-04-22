package no.kristiania.pgr208_1.exam

import android.text.InputFilter
import android.text.Spanned
import java.lang.Double.parseDouble
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Matcher
import java.util.regex.Pattern

const val NOT_INSERTED = "no.kristiania.pgr208_1.pgr208_1_exam.NOT_INSERTED"
const val TRANSACTION_INITIAL = "no.kristiania.pgr208_1.pgr208_1_exam.TRANSACTION_INITIAL"
const val EXTRA_CURRENCY_ID = "no.kristiania.pgr208_1.pgr208_1_exam.CURRENCY_ID"
const val EXTRA_CURRENCY_SYMBOL = "no.kristiania.pgr208_1.pgr208_1_exam.CURRENCY_SYMBOL"


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

fun round(number: String, decimalAmount: Int?): String {
    return round(number.toDouble(), decimalAmount).toString()
}

class DecimalDigitsInputFilter(val decimalDigits: Int) : InputFilter {


    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        var dotPos = dest!!.indexOf(".")
        if(dotPos == 0) return null
        if (dotPos > 0) {
            // if the text is entered before the dot
            if (dend <= dotPos) {
                return null
            }
            if (dest.length - dotPos > decimalDigits) {
                return ""
            }
        }

        return null
    }
}

class InputFilterMinMax(min:Float, max:Float): InputFilter {
    private var min:Float = 0.0F
    private var max:Float = 0.0F

    init{
        this.min = min
        this.max = max
    }

    override fun filter(source:CharSequence, start:Int, end:Int, dest: Spanned, dstart:Int, dend:Int): CharSequence? {
        try
        {
            val input = (dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length)).toFloat()
            if (isInRange(min, max, input))
                return null
        }
        catch (nfe:NumberFormatException) {}
        return ""
    }

    private fun isInRange(a:Float, b:Float, c:Float):Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}