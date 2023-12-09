package ru.sokolovromann.myshopping.ui.utils

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.utils.isCurrentYear
import ru.sokolovromann.myshopping.data.utils.isToday
import ru.sokolovromann.myshopping.data.utils.isTomorrow
import ru.sokolovromann.myshopping.data.utils.isYesterday
import ru.sokolovromann.myshopping.ui.model.UiString
import java.util.Calendar

fun Calendar.getDisplayDateAndTime(): UiString = when {
    isYesterday() -> UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_yesterdayAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isToday() -> UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_todayAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isTomorrow() -> UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_tomorrowAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isCurrentYear() -> UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_currentYearAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis)
    )

    else -> UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_otherAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis)
    )
}

fun Calendar.getDisplayDate(): UiString = when {
    isYesterday() -> UiString.FromResources(id = R.string.calendar_text_yesterday)

    isToday() -> UiString.FromResources(id = R.string.calendar_text_today)

    isTomorrow() -> UiString.FromResources(id = R.string.calendar_text_tomorrow)

    isCurrentYear() -> UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_currentYear,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    else -> UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_other,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )
}

fun Calendar.getDisplayTime(): UiString {
    return UiString.FromResourcesWithArgs(
        id = R.string.calendar_text_time,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )
}

@Composable
fun Calendar.isTime24HourFormat(): Boolean {
    return DateFormat.is24HourFormat(LocalContext.current)
}