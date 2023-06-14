package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditShoppingListTotalScreenEvent {

    object ShowBackScreen : EditShoppingListTotalScreenEvent()

    data class ShowBackScreenAndUpdateProductsWidget(val shoppingUid: String) : EditShoppingListTotalScreenEvent()

    object ShowKeyboard : EditShoppingListTotalScreenEvent()
}
