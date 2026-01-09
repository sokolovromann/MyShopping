package ru.sokolovromann.myshopping.data39.suggestions

import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime

data class SuggestionDetailValue<D>(
    val uid: UID,
    val directory: UID,
    val created: DateTime,
    val data: D
)
