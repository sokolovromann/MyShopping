package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditShoppingListNameScreenEvent {

    data class OnShowBackScreen(val shoppingUid: String) : EditShoppingListNameScreenEvent()

    object OnShowKeyboard : EditShoppingListNameScreenEvent()
}