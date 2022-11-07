package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditCurrencySymbolEvent {

    object SaveCurrencySymbol : EditCurrencySymbolEvent()

    object CancelSavingCurrencySymbol : EditCurrencySymbolEvent()

    data class CurrencySymbolChanged(val value: TextFieldValue) : EditCurrencySymbolEvent()
}