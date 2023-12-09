package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditTaxRateEvent {

    object OnClickSave : EditTaxRateEvent()

    object OnClickCancel : EditTaxRateEvent()

    data class OnTaxRateChanged(val value: TextFieldValue) : EditTaxRateEvent()
}