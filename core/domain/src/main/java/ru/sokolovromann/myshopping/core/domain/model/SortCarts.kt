package ru.sokolovromann.myshopping.core.domain.model

sealed class SortCarts {

    data class ByCreated(val byAscending: Boolean) : SortCarts()

    data class ByLastModified(val byAscending: Boolean) : SortCarts()

    data class ByName(val byAscending: Boolean) : SortCarts()

    data class ByTotal(val byAscending: Boolean) : SortCarts()

    data class ByReminder(val byAscending: Boolean) : SortCarts()

    data object DoNotSort : SortCarts()

    fun isByAscending(): Boolean? = when (this) {
        is ByCreated -> byAscending
        is ByLastModified -> byAscending
        is ByName -> byAscending
        is ByTotal -> byAscending
        is ByReminder -> byAscending
        DoNotSort -> null
    }

    fun isByDescending(): Boolean? = isByAscending()?.let { !it }
}