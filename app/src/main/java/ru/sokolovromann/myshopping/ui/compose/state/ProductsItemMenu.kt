package ru.sokolovromann.myshopping.ui.compose.state

data class ProductsItemMenu(
    val editBody: TextData = TextData(),
    val deleteBody: TextData = TextData(),
    val copyToShoppingListBody: TextData = TextData(),
    val moveToShoppingListBody: TextData = TextData(),
)