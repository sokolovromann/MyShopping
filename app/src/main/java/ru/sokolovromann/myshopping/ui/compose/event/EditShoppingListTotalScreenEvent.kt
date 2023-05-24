package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditShoppingListTotalScreenEvent {

    object ShowBackScreen : EditShoppingListTotalScreenEvent()

    object ShowKeyboard : EditShoppingListTotalScreenEvent()
}
