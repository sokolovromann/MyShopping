package ru.sokolovromann.myshopping.data39.suggestions

sealed class SortSuggestions {

    enum class Order {
        ByAscending, ByDescending
    }

    data class Name(val order: Order) : SortSuggestions()

    data class Popularity(val order: Order) : SortSuggestions()
}