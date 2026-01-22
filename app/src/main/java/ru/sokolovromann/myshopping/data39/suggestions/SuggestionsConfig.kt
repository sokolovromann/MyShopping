package ru.sokolovromann.myshopping.data39.suggestions

data class SuggestionsConfig(
    val preInstalled: SuggestionsPreInstalled,
    val viewMode: SuggestionsViewMode,
    val sort: SortSuggestions,
    val add: AddSuggestionWithDetails,
    val takeSuggestions: TakeSuggestions,
    val takeDetails: TakeSuggestionDetailsInfo
)