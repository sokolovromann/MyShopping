package ru.sokolovromann.myshopping.core.domain.model

sealed class DateTimeFormattingMode {

    data class DDMMMYYYY(val is24hour: Boolean) : DateTimeFormattingMode()

    data class MMMDDYYYY(val is24hour: Boolean) : DateTimeFormattingMode()

    data class YYYYMMMDD(val is24hour: Boolean) : DateTimeFormattingMode()

    fun is24HourFormat(): Boolean = when (this) {
        is DDMMMYYYY -> is24hour
        is MMMDDYYYY -> is24hour
        is YYYYMMMDD -> is24hour
    }
}