package ru.sokolovromann.myshopping.data39.suggestions

data class SuggestionsConfig(
    val viewMode: SuggestionsViewMode,
    val sort: SortSuggestions,
    val takeSuggestions: TakeSuggestions,
    val takeDetails: TakeSuggestionDetailsInfo
)