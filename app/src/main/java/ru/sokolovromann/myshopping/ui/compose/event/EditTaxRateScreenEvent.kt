package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditTaxRateScreenEvent {

    object ShowBackScreen : EditTaxRateScreenEvent()

    object ShowBackScreenAndUpdateProductsWidgets : EditTaxRateScreenEvent()

    object ShowKeyboard : EditTaxRateScreenEvent()
}