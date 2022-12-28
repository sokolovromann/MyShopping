package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditShoppingListNameScreenEvent {

    object ShowBackScreen : EditShoppingListNameScreenEvent()

    object ShowKeyboard : EditShoppingListNameScreenEvent()
}