package ru.sokolovromann.myshopping.core.domain.model

data class SuggestionWithFilteredFabrics(
    val suggestion: Suggestion,
    val fabrics: FilteredFabricsByType
)