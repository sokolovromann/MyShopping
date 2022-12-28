package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditTaxRateScreenEvent {

    object ShowBackScreen : EditTaxRateScreenEvent()

    object ShowKeyboard : EditTaxRateScreenEvent()
}