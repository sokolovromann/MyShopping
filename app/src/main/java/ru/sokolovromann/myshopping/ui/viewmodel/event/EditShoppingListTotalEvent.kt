package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditShoppingListTotalEvent {

    object SaveShoppingListTotal : EditShoppingListTotalEvent()

    object CancelSavingShoppingListTotal : EditShoppingListTotalEvent()

    data class ShoppingListTotalChanged(val value: TextFieldValue) : EditShoppingListTotalEvent()
}
