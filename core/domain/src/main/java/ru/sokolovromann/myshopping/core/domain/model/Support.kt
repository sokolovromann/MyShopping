package ru.sokolovromann.myshopping.core.domain.model

data class Support(
    val uid: UID,
    val directory: SuggestionDirectory,
    val created: TimeInMillis,
    val lastModified: TimeInMillis,
    val name: String,
    val filteredFabricsByType: FilteredFabricsByType,
    val used: Int
)