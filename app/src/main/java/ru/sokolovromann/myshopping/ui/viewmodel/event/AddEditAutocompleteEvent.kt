package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class AddEditAutocompleteEvent {

    object SaveAutocomplete : AddEditAutocompleteEvent()

    object CancelSavingAutocomplete : AddEditAutocompleteEvent()

    data class NameChanged(val value: TextFieldValue) : AddEditAutocompleteEvent()
}