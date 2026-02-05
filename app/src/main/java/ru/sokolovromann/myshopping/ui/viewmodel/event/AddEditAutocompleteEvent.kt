package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.utils.UID

sealed class AddEditAutocompleteEvent {

    object OnClickSave : AddEditAutocompleteEvent()

    object OnClickCancel : AddEditAutocompleteEvent()

    data class OnNameValueChanged(val value: TextFieldValue) : AddEditAutocompleteEvent()

    data class OnClickDeleteDetail(val uid: UID) : AddEditAutocompleteEvent()
}