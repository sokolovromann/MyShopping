package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditShoppingListTotalScreenEvent {

    data class OnShowBackScreen(val shoppingUid: String) : EditShoppingListTotalScreenEvent()

    object OnShowKeyboard : EditShoppingListTotalScreenEvent()
}
