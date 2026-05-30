package ru.sokolovromann.myshopping.core.domain.model

sealed class SortProducts {

    data class ByCreated(val byAscending: Boolean) : SortProducts()

    data class ByLastModified(val byAscending: Boolean) : SortProducts()

    data class ByName(val byAscending: Boolean) : SortProducts()

    data class ByCost(val byAscending: Boolean) : SortProducts()

    data object DoNotSort : SortProducts()

    fun isByAscending(): Boolean? = when (this) {
        is ByCreated -> byAscending
        is ByLastModified -> byAscending
        is ByName -> byAscending
        is ByCost -> byAscending
        DoNotSort -> null
    }

    fun isByDescending(): Boolean? = isByAscending()?.let { !it }
}