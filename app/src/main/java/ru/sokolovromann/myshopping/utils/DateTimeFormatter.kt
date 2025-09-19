package ru.sokolovromann.myshopping.utils

import java.util.Calendar
import java.util.Locale

class DateTimeFormatter(
    private val dateFormattingMode: DateFormattingMode,
    private val timeFormattingMode: TimeFormattingMode
) {

    fun getDisplay(dateTime: DateTime): String {
        val calendar = dateTime.getCalendar()
        val dateDisplay = when (dateFormattingMode) {
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
        val timeDisplay = when (timeFormattingMode) {
            TimeFormattingMode.H12 -> "%tI:%tM %tp"
            TimeFormattingMode.H24 -> "%tH:%tM"
            TimeFormattingMode.Timestamp -> "%tH%tM%tS"
        }

        val args = LongArray(6)
        for (i:Int in 0..5) {
            args[i] = dateTime.getMillis()
        }

        return String.format(
            locale = Locale.getDefault(),
            format = "$dateDisplay$timeDisplay",
            args = args.toTypedArray()
        )
    }

    private fun Calendar.isCurrentYear(): Boolean {
        return get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
    }

    private fun Calendar.isToday(): Boolean {
        return isCurrentYear() && get(Calendar.DAY_OF_YEAR) ==
                Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }
}