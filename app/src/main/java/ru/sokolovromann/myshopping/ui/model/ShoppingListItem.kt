package ru.sokolovromann.myshopping.ui.model

data class ShoppingListItem(
    val uid: String,
    val name: UiString,
    val products: List<Pair<Boolean?, UiString>>,
    val total: UiString,
    val reminder: UiString,
    val completed: Boolean
)