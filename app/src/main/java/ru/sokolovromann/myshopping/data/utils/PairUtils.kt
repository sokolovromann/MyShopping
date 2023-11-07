package ru.sokolovromann.myshopping.data.utils

import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingList

fun Pair<List<Product>, List<Product>>.toProductsList(): List<Product> {
    return mutableListOf<Product>().apply { 
        addAll(first)
        addAll(second)
    }.toList()
}

fun Pair<List<ShoppingList>, List<ShoppingList>>.toShoppingsList(): List<ShoppingList> {
    return mutableListOf<ShoppingList>().apply {
        addAll(first)
        addAll(second)
    }.toList()
}

fun Pair<List<Autocomplete>, List<Autocomplete>>.toAutocompletesList(): List<Autocomplete> {
    return mutableListOf<Autocomplete>().apply {
        addAll(first)
        addAll(second)
    }.toList()
}