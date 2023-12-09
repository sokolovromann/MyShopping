package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditTaxRateScreenEvent {

    object OnShowBackScreen : EditTaxRateScreenEvent()

    object OnShowKeyboard : EditTaxRateScreenEvent()
}