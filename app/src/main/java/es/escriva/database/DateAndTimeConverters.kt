package es.escriva.database

import androidx.room.TypeConverter
import es.escriva.config.Constants
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateAndTimeConverters {

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            return LocalDateTime.parse(it, Constants().dateTimeFormatter)
        }
    }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(Constants().dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            return LocalDate.parse(it, Constants().dateFormatter)
        }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(Constants().dateFormatter)
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let {
            return LocalTime.parse(it, Constants().timeFormatter)
        }
    }

    @TypeConverter
    fun fromLocalTime(date: LocalTime?): String? {
        return date?.format(Constants().timeFormatter)
    }

}