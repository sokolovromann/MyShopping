package ru.sokolovromann.myshopping.data.repository.model

data class Sort(
    val sortBy: SortBy = SortBy.DefaultValue,
    val ascending: Boolean = true
)