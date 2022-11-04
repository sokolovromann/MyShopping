package ru.sokolovromann.myshopping.ui

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import java.util.*
import java.util.Calendar.getInstance

fun Calendar.getDisplayDateAndTime(): UiText = when {
    isYesterday() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_yesterdayAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isToday() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_todayAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isTomorrow() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_tomorrowAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isCurrentYear() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_currentYearAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis)
    )

    else -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_otherAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis)
    )
}

fun Calendar.getDisplayDate(): UiText = when {
    isYesterday() -> UiText.FromResources(id = R.string.calendar_yesterday)

    isToday() -> UiText.FromResources(id = R.string.calendar_today)

    isTomorrow() -> UiText.FromResources(id = R.string.calendar_tomorrow)

    isCurrentYear() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_currentYear,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    else -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_other,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )
}

fun Calendar.getDisplayTime(): UiText {
    return UiText.FromResourcesWithArgs(
        id = R.string.calendar_time,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )
}

private fun Calendar.isCurrentYear(): Boolean {
    return get(Calendar.YEAR) == getInstance().get(Calendar.YEAR)
}

private fun Calendar.isTomorrow(): Boolean {
    val calendar = getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
    }
    return isCurrentYear() && get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
}

private fun Calendar.isToday(): Boolean {
    return isCurrentYear() && get(Calendar.DAY_OF_YEAR) == getInstance().get(Calendar.DAY_OF_YEAR)
}

private fun Calendar.isYesterday(): Boolean {
    val calendar = getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return isCurrentYear() && get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
}