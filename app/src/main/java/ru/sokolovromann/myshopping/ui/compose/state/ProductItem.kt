package ru.sokolovromann.myshopping.ui.compose.state

data class ProductItem(
    val uid: String = "",
    val nameText: UiText = UiText.Nothing,
    val bodyText: UiText = UiText.Nothing,
    val completed: Boolean = false
)