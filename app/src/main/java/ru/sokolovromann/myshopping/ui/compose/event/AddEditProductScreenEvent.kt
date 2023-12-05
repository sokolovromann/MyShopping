package ru.sokolovromann.myshopping.ui.compose.event

sealed class AddEditProductScreenEvent {

    data class OnShowBackScreen(val shoppingUid: String) : AddEditProductScreenEvent()

    object OnShowKeyboard : AddEditProductScreenEvent()
}