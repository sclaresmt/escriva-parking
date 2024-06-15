package es.escriva.config

import java.time.format.DateTimeFormatter

class Constants {

    private val timePattern = "HH:mm:ss";

    private val datePattern = "dd/MM/yyyy";

    val dateTimeFormatter =
        DateTimeFormatter.ofPattern(String.format("%s %s", datePattern, timePattern))!!

    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datePattern)

    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timePattern)

}