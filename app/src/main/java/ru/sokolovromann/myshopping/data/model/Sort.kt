package ru.sokolovromann.myshopping.data.model

data class Sort(
    val sortBy: SortBy = SortBy.DefaultValue,
    val ascending: Boolean = true
)