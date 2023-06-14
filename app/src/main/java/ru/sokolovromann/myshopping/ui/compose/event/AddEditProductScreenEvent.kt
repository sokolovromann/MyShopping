package ru.sokolovromann.myshopping.ui.compose.event

sealed class AddEditProductScreenEvent {

    object ShowBackScreen : AddEditProductScreenEvent()

    data class ShowBackScreenAndUpdateProductsWidget(val shoppingUid: String) : AddEditProductScreenEvent()

    object ShowKeyboard : AddEditProductScreenEvent()
}