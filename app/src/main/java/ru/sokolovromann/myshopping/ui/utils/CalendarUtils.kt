package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.utils.isCurrentYear
import ru.sokolovromann.myshopping.data.utils.isToday
import ru.sokolovromann.myshopping.data.utils.isTomorrow
import ru.sokolovromann.myshopping.data.utils.isYesterday
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import java.util.*

@Deprecated("Use getDisplayDateAndTime")
fun Calendar.getDisplayDateAndTimeText(): UiText = when {
    isYesterday() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_text_yesterdayAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isToday() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_text_todayAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isTomorrow() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_text_tomorrowAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis)
    )

    isCurrentYear() -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_text_currentYearAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis)
    )

    else -> UiText.FromResourcesWithArgs(
        id = R.string.calendar_text_otherAt,
        args = arrayOf(timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis, timeInMillis)
    )
}