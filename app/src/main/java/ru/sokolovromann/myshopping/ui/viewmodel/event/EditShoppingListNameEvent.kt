package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditShoppingListNameEvent {

    object SaveShoppingListName : EditShoppingListNameEvent()

    object CancelSavingShoppingListName : EditShoppingListNameEvent()

    data class ShoppingListNameChanged(val value: TextFieldValue) : EditShoppingListNameEvent()
}