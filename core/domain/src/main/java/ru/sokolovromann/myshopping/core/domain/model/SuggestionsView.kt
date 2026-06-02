package ru.sokolovromann.myshopping.core.domain.model

sealed class SuggestionsView {

    data class List(val displayMode: SuggestionsFieldsDisplayMode) : SuggestionsView()

    data class Grid(val displayMode: SuggestionsFieldsDisplayMode) : SuggestionsView()

    fun getDisplayMode() = when (this) {
        is List -> displayMode
        is Grid -> displayMode
    }
}