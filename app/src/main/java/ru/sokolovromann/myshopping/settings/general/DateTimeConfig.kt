package ru.sokolovromann.myshopping.settings.general

import ru.sokolovromann.myshopping.utils.DateFormattingMode
import ru.sokolovromann.myshopping.utils.TimeFormattingMode

data class DateTimeConfig(
    val dateFormattingMode: DateFormattingMode,
    val timeFormattingMode: TimeFormattingMode
)