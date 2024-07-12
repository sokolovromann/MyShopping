package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class EditShoppingListTotalEvent {

    object OnClickSave : EditShoppingListTotalEvent()

    object OnClickCancel : EditShoppingListTotalEvent()

    data class OnTotalChanged(val value: TextFieldValue) : EditShoppingListTotalEvent()

    data class OnDiscountChanged(val value: TextFieldValue) : EditShoppingListTotalEvent()

    data class OnDiscountAsPercentSelected(val asPercent: Boolean) : EditShoppingListTotalEvent()

    data class OnSelectDiscountAsPercent(val expanded: Boolean) : EditShoppingListTotalEvent()
}
