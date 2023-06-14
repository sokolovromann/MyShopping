package ru.sokolovromann.myshopping.ui.compose.state

data class ProductWidgetItem(
    val uid: String,
    val name: String,
    val body: String,
    val completed: Boolean
)