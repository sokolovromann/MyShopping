package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditCurrencySymbolScreenEvent {

    object ShowBackScreen : EditCurrencySymbolScreenEvent()

    object ShowKeyboard : EditCurrencySymbolScreenEvent()
}