package br.com.alura.orgs.database.converts

import androidx.room.TypeConverter
import java.math.BigDecimal

class Converts {

    @TypeConverter
    fun fromDuble(value: Double? ) : BigDecimal =
        value?.let { BigDecimal(value.toString()) } ?: BigDecimal.ZERO

    @TypeConverter
    fun bigDecimalToDouble(value: BigDecimal? ) : Double? =
        value?.let { value.toDouble() }

}