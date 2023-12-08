package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditShoppingListTotalEvent {

    object OnClickSave : EditShoppingListTotalEvent()

    object OnClickCancel : EditShoppingListTotalEvent()

    data class OnTotalChanged(val value: TextFieldValue) : EditShoppingListTotalEvent()
}
