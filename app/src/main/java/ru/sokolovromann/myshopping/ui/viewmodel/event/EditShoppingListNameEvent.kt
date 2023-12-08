package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditShoppingListNameEvent {

    object OnClickSave : EditShoppingListNameEvent()

    object OnClickCancel : EditShoppingListNameEvent()

    data class OnNameChanged(val value: TextFieldValue) : EditShoppingListNameEvent()
}