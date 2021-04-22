package no.kristiania.pgr208_1.exam

import android.text.InputFilter
import android.text.Spanned
import androidx.room.TypeConverter
import java.lang.Double.parseDouble
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

const val NOT_INSERTED = "no.kristiania.pgr208_1.pgr208_1_exam.NOT_INSERTED"
const val TRANSACTION_INITIAL = "no.kristiania.pgr208_1.pgr208_1_exam.TRANSACTION_INITIAL"
const val EXTRA_CURRENCY_ID = "no.kristiania.pgr208_1.pgr208_1_exam.CURRENCY_ID"
const val EXTRA_CURRENCY_SYMBOL = "no.kristiania.pgr208_1.pgr208_1_exam.CURRENCY_SYMBOL"

// Rounding function for dynamic rounding based off of number of digits left of decimal
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

// Parse string and call on rounding function
fun round(number: String, decimalAmount: Int?): String {
    return round(number.toDouble(), decimalAmount).toString()
}

// InputFilter to prevent unlimited use of decimal digits
class DecimalDigitsInputFilter(val decimalDigits: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
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

object DateConverters {
    @TypeConverter @JvmStatic
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter @JvmStatic
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}




