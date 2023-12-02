package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class AddEditAutocompleteEvent {

    object OnClickSave : AddEditAutocompleteEvent()

    object OnClickCancel : AddEditAutocompleteEvent()

    data class OnNameValueChanged(val value: TextFieldValue) : AddEditAutocompleteEvent()
}