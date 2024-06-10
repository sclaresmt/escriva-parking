package es.escriva.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {

    private val timePattern = "HH:mm:ss";

    private val datePattern = "dd/MM/yyyy";

    private val dateTimeFormatter = DateTimeFormatter.ofPattern(String.format("%s %s", datePattern, timePattern))

    private val dateFormatter = DateTimeFormatter.ofPattern(datePattern)

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            return LocalDateTime.parse(it, dateTimeFormatter)
        }
    }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            return LocalDate.parse(it, dateFormatter)
        }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

}