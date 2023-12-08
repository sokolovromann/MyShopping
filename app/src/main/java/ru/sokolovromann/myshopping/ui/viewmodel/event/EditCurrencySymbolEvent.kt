package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditCurrencySymbolEvent {

    object OnClickSave : EditCurrencySymbolEvent()

    object OnClickCancel : EditCurrencySymbolEvent()

    data class OnSymbolChanged(val value: TextFieldValue) : EditCurrencySymbolEvent()
}