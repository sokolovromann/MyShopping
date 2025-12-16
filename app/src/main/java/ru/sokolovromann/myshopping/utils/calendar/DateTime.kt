package ru.sokolovromann.myshopping.utils.calendar

import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar

class DateTime(private val value: Long) {

    companion object {
        fun getCurrent(): DateTime {
            return DateTime(System.currentTimeMillis())
        }
    }

    fun add(dateInfo: DateInfo): DateTime {
        val calendar = toCalendar().apply {
            add(Calendar.YEAR, dateInfo.year)
            add(Calendar.MONTH, dateInfo.month)
            add(Calendar.DAY_OF_MONTH, dateInfo.dayOfMonth)
        }
        return DateTime(calendar.timeInMillis)
    }

    fun add(timeInfo: TimeInfo): DateTime {
        val calendar = toCalendar().apply {
            if (timeInfo.is24Hour) {
                add(Calendar.HOUR_OF_DAY, timeInfo.hour)
            } else {
                add(Calendar.HOUR, timeInfo.hour)
            }
            add(Calendar.MINUTE, timeInfo.minute)
            add(Calendar.SECOND, timeInfo.second)
        }
        return DateTime(calendar.timeInMillis)
    }

    fun set(dateInfo: DateInfo): DateTime {
        val calendar = toCalendar().apply {
            set(Calendar.YEAR, dateInfo.year)
            set(Calendar.MONTH, dateInfo.month)
            set(Calendar.DAY_OF_MONTH, dateInfo.dayOfMonth)
        }
        return DateTime(calendar.timeInMillis)
    }

    fun set(timeInfo: TimeInfo): DateTime {
        val calendar = toCalendar().apply {
            if (timeInfo.is24Hour) {
                set(Calendar.HOUR_OF_DAY, timeInfo.hour)
            } else {
                set(Calendar.HOUR, timeInfo.hour)
            }
            set(Calendar.MINUTE, timeInfo.minute)
            set(Calendar.SECOND, timeInfo.second)
        }
        return DateTime(calendar.timeInMillis)
    }

    fun getDate(): DateInfo {
        val calendar = toCalendar()
        return DateInfo(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun getTime(context: Context): TimeInfo {
        val is24Hour = DateFormat.is24HourFormat(context)
        val hourField: Int = if (is24Hour) Calendar.HOUR_OF_DAY else Calendar.HOUR
        val calendar = toCalendar()
        return TimeInfo(
            hour = calendar.get(hourField),
            is24Hour = is24Hour,
            minute = calendar.get(Calendar.MINUTE),
            second = calendar.get(Calendar.SECOND)
        )
    }

    fun getMillis(): Long {
        return value
    }

    fun toCalendar(): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = value
        }
    }

    override fun toString(): String {
        return getMillis().toString()
    }
}