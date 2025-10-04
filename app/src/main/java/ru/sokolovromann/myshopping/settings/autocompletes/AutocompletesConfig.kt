package ru.sokolovromann.myshopping.settings.autocompletes

data class AutocompletesConfig(
    val viewMode: AutocompletesViewMode,
    val sort: SortAutocompletes,
    val maxNumber: MaxAutocompletesNumber
)