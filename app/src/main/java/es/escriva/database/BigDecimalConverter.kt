package es.escriva.database

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class BigDecimalConverter {
    private val df = DecimalFormat("#.0", DecimalFormatSymbols(Locale("es", "ES")))

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String {
        return df.format(value)
    }

    @TypeConverter
    fun toBigDecimal(value: String): BigDecimal {
        return BigDecimal(df.parse(value)!!.toString())
    }
}