package com.netwatcher.polaris.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun TimeStampConverter(timestamp: String): String {
    val inputFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
    val dateTime = LocalDateTime.parse(timestamp, inputFormat)
    return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z"
}