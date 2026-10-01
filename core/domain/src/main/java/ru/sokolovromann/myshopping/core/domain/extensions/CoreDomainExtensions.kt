package ru.sokolovromann.myshopping.core.domain.extensions

import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import java.util.Calendar
import java.util.Locale

fun Calendar.getDisplayDateTime(formattingMode: DateTimeFormattingMode): String {
    val isCurrentYear = get(Calendar.YEAR) ==
            Calendar.getInstance().get(Calendar.YEAR)
    val isToday = isCurrentYear && get(Calendar.DAY_OF_YEAR) ==
            Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    val dateDisplay = when (formattingMode) {
        is DateTimeFormattingMode.DDMMMYYYY -> when {
            isToday -> ""
            isCurrentYear -> "%td %tb "
            else -> "%td %tb %tY "
        }
        is DateTimeFormattingMode.MMMDDYYYY -> when {
            isToday -> ""
            isCurrentYear -> "%tb %td, "
            else -> "%tb %td %tY, "
        }
        is DateTimeFormattingMode.YYYYMMMDD -> when {
            isToday -> ""
            isCurrentYear -> "%tb %td "
            else -> "%tY %tb %td "
        }
    }
    val timeDisplay = if (formattingMode.is24HourFormat()) {
        "%tH:%tM"
    } else {
        "%tI:%tM %tp"
    }
    val millis = LongArray(6)
    for (i:Int in 0..5) { millis[i] = timeInMillis }
    return String.format(
        locale = Locale.getDefault(),
        format = "$dateDisplay$timeDisplay",
        args = millis.toTypedArray()
    )
}

fun Calendar.getDisplayTimestamp(): String {
    val millis = LongArray(6)
    for (i:Int in 0..5) { millis[i] = timeInMillis }
    return String.format(
        locale = Locale.getDefault(),
        format = "%tY%tm%td_%tH%tM%tS",
        args = millis.toTypedArray()
    )
}