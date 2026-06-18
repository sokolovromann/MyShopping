package ru.sokolovromann.myshopping.core.domain.model

data class Suggestion(
    val uid: UID,
    val directory: SuggestionDirectory,
    val created: TimeInMillis,
    val lastModified: TimeInMillis,
    val name: String,
    val used: Int
)