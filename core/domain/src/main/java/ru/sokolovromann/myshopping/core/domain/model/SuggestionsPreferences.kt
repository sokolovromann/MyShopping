package ru.sokolovromann.myshopping.core.domain.model

data class SuggestionsPreferences(
    val view: SuggestionsView,
    val sort: SortSuggestions,
    val addingMode: SuggestionAddingMode,
    val displaySuggestionNames: DisplaySuggestionNames,
    val displaySuggestionDetails: DisplaySuggestionDetails
)