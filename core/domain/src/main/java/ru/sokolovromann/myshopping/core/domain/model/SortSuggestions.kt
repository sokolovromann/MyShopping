package ru.sokolovromann.myshopping.core.domain.model

sealed class SortSuggestions {

    data class ByName(val byAscending: Boolean) : SortSuggestions()

    data class ByPopularity(val byAscending: Boolean) : SortSuggestions()

    fun isByAscending(): Boolean = when (this) {
        is ByName -> byAscending
        is ByPopularity -> byAscending
    }

    fun isByDescending(): Boolean = !isByAscending()
}