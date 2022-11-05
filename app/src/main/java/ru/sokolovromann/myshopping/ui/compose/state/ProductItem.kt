package ru.sokolovromann.myshopping.ui.compose.state

data class ProductItem(
    val uid: String = "",
    val title: TextData = TextData(),
    val body: TextData = TextData(),
    val completed: CheckboxData = CheckboxData()
)