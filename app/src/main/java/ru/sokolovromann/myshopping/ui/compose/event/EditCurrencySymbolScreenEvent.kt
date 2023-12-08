package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditCurrencySymbolScreenEvent {

    object OnShowBackScreen : EditCurrencySymbolScreenEvent()

    object OnShowKeyboard : EditCurrencySymbolScreenEvent()
}