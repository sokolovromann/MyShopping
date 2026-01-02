package ru.sokolovromann.myshopping.data39.suggestions

import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime

data class Suggestion(
    val uid: UID,
    val directory: SuggestionDirectory,
    val created: DateTime,
    val lastModified: DateTime,
    val name: String,
    val used: Int
)