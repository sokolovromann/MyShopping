package ru.sokolovromann.myshopping.ui.model

data class ProductItem(
    val uid: String,
    val name: UiString,
    val body: UiString,
    val completed: Boolean
)