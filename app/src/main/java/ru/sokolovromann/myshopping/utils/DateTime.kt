package ru.sokolovromann.myshopping.utils

import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar

class DateTime private constructor(private val calendar: Calendar) {

    companion object {

        fun getCurrent(): DateTime {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.MILLISECOND, 0)
            }
            return DateTime(calendar)
        }

        fun createOrNull(millis: Long?): DateTime? {
            return millis?.let {
                val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                DateTime(calendar)
            }
        }

        fun createOrDefault(millis: Long?, defaultValue: DateTime = getCurrent()): DateTime {
            return createOrNull(millis) ?: defaultValue
        }
    }

    fun add(dateInfo: DateInfo): DateTime {
        val newCalendar = calendar.apply {
            add(Calendar.YEAR, dateInfo.year)
            add(Calendar.MONTH, dateInfo.month)
            add(Calendar.DAY_OF_MONTH, dateInfo.dayOfMonth)
        }
        return DateTime(newCalendar)
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
        return DateTime(newCalendar)
    }

    fun set(dateInfo: DateInfo): DateTime {
        val newCalendar = calendar.apply {
            set(Calendar.YEAR, dateInfo.year)
            set(Calendar.MONTH, dateInfo.month)
            set(Calendar.DAY_OF_MONTH, dateInfo.dayOfMonth)
        }
        return DateTime(newCalendar)
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
        return DateTime(newCalendar)
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

    fun getCalendar(): Calendar {
        return calendar
    }

    fun getMillis(): Long {
        return calendar.timeInMillis
    }

    fun getString(): String {
        return calendar.timeInMillis.toString()
    }

    override fun toString(): String {
        return getString()
    }
}