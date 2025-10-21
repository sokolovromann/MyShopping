package ru.sokolovromann.myshopping.utils

import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.text.toLong

class DateTime private constructor(
    private val calendar: Calendar,
    private val config: DateTimeConfig
) {

    companion object {

        fun fromString(millis: String, config: DateTimeConfig): DateTime {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = millis.toLong()
            }
            return DateTime(calendar, config)
        }

        fun getCurrentMillis(): Long {
            return System.currentTimeMillis()
        }
    }

    fun newMillis(millis: Long): DateTime {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        return DateTime(calendar, config)
    }

    fun newMillis(millis: String): DateTime {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis.toLong()
        }
        return DateTime(calendar, config)
    }

    fun add(dateInfo: DateInfo): DateTime {
        val newCalendar = calendar.apply {
            add(Calendar.YEAR, dateInfo.year)
            add(Calendar.MONTH, dateInfo.month)
            add(Calendar.DAY_OF_MONTH, dateInfo.dayOfMonth)
        }
        return DateTime(newCalendar, config)
    }

    fun add(timeInfo: TimeInfo): DateTime {
        val newCalendar = calendar.apply {
            if (timeInfo.is24Hour) {
                add(Calendar.HOUR_OF_DAY, timeInfo.hour)
            } else {
                add(Calendar.HOUR, timeInfo.hour)
            }
            add(Calendar.MINUTE, timeInfo.minute)
            add(Calendar.SECOND, timeInfo.second)
            add(Calendar.MILLISECOND, 0)
        }
        return DateTime(newCalendar, config)
    }

    fun set(dateInfo: DateInfo): DateTime {
        val newCalendar = calendar.apply {
            set(Calendar.YEAR, dateInfo.year)
            set(Calendar.MONTH, dateInfo.month)
            set(Calendar.DAY_OF_MONTH, dateInfo.dayOfMonth)
        }
        return DateTime(newCalendar, config)
    }

    fun set(timeInfo: TimeInfo): DateTime {
        val newCalendar = calendar.apply {
            if (timeInfo.is24Hour) {
                set(Calendar.HOUR_OF_DAY, timeInfo.hour)
            } else {
                set(Calendar.HOUR, timeInfo.hour)
            }
            set(Calendar.MINUTE, timeInfo.minute)
            set(Calendar.SECOND, timeInfo.second)
            set(Calendar.MILLISECOND, 0)
        }
        return DateTime(newCalendar, config)
    }

    fun getDate(): DateInfo {
        return DateInfo(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun getTime(context: Context): TimeInfo {
        val is24Hour = DateFormat.is24HourFormat(context)
        val hourField: Int = if (is24Hour) Calendar.HOUR_OF_DAY else Calendar.HOUR
        return TimeInfo(
            hour = calendar.get(hourField),
            is24Hour = is24Hour,
            minute = calendar.get(Calendar.MINUTE),
            second = calendar.get(Calendar.SECOND)
        )
    }

    fun getMillis(): Long {
        return calendar.timeInMillis
    }

    fun getDisplay(): String {
        val dateDisplay = when (config.dateFormattingMode) {
            DateFormattingMode.DDMMMYYYY -> when {
                calendar.isToday() -> ""
                calendar.isCurrentYear() -> "%td %tb "
                else -> "%td %tb %tY "
            }
            DateFormattingMode.MMMDDYYYY -> when {
                calendar.isToday() -> ""
                calendar.isCurrentYear() -> "%tb %td, "
                else -> "%tb %td %tY, "
            }
            DateFormattingMode.YYYYMMMDD -> when {
                calendar.isToday() -> ""
                calendar.isCurrentYear() -> "%tb %td "
                else -> "%tY %tb %td "
            }
            DateFormattingMode.Timestamp -> "%tY%tm%td"
        }
        val timeDisplay = when (config.timeFormattingMode) {
            TimeFormattingMode.H12 -> "%tI:%tM %tp"
            TimeFormattingMode.H24 -> "%tH:%tM"
            TimeFormattingMode.Timestamp -> "%tH%tM%tS"
        }

        val args = LongArray(6)
        for (i:Int in 0..5) {
            args[i] = getMillis()
        }

        return String.format(
            locale = Locale.getDefault(),
            format = "$dateDisplay$timeDisplay",
            args = args.toTypedArray()
        )
    }

    fun getString(): String {
        return getMillis().toString()
    }

    override fun toString(): String {
        return getDisplay()
    }

    private fun Calendar.isCurrentYear(): Boolean {
        return get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
    }

    private fun Calendar.isToday(): Boolean {
        return isCurrentYear() && get(Calendar.DAY_OF_YEAR) ==
                Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }
}