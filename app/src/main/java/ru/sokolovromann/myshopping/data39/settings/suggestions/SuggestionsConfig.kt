package ru.sokolovromann.myshopping.data39.settings.suggestions

data class SuggestionsConfig(
    val viewMode: SuggestionsViewMode,
    val sort: SortSuggestions,
    val take: TakeSuggestions
)