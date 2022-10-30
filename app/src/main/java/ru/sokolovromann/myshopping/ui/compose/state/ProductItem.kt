package ru.sokolovromann.myshopping.ui.compose.state

data class ProductItem(
    val uid: String = "",
    val title: TextData = TextData.Title,
    val body: TextData = TextData.Body,
    val completed: Boolean = false
)