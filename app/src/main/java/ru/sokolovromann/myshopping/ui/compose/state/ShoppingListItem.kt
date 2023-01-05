package ru.sokolovromann.myshopping.ui.compose.state

data class ShoppingListItem(
    val uid: String = "",
    val nameText: UiText = UiText.Nothing,
    val productsList: List<Pair<Boolean?, UiText>> = listOf(),
    val totalText: UiText = UiText.Nothing,
    val reminderText: UiText = UiText.Nothing
)