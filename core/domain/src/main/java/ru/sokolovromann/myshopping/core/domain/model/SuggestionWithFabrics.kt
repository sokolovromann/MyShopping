package ru.sokolovromann.myshopping.core.domain.model

data class SuggestionWithFabrics(
    val suggestion: Suggestion,
    val fabrics: Collection<Fabric>
)