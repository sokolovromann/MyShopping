package ru.sokolovromann.myshopping.ui.compose.state

data class ProductsMenu(
    val editNameBody: TextData = TextData(),
    val editReminderBody: TextData = TextData(),
    val calculateChangeBody: TextData = TextData(),
    val deleteProductsBody: TextData = TextData(),
    val shareBody: TextData = TextData(),
)