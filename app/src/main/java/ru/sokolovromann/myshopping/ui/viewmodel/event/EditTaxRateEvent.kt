package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditTaxRateEvent {

    object SaveTaxRate : EditTaxRateEvent()

    object CancelSavingTaxRate : EditTaxRateEvent()

    data class TaxRateChanged(val value: TextFieldValue) : EditTaxRateEvent()
}