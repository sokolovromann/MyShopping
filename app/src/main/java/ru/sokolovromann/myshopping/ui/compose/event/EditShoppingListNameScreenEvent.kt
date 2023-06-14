package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditShoppingListNameScreenEvent {

    object ShowBackScreen : EditShoppingListNameScreenEvent()

    data class ShowBackScreenAndUpdateProductsWidget(val shoppingUid: String) : EditShoppingListNameScreenEvent()

    object ShowKeyboard : EditShoppingListNameScreenEvent()
}