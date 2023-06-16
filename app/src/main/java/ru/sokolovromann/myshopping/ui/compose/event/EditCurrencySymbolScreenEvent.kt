package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditCurrencySymbolScreenEvent {

    object ShowBackScreen : EditCurrencySymbolScreenEvent()

    object ShowBackScreenAndUpdateProductsWidgets : EditCurrencySymbolScreenEvent()

    object ShowKeyboard : EditCurrencySymbolScreenEvent()
}