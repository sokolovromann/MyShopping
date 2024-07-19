package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.DisplayTotal

sealed class EditShoppingListTotalEvent {

    object OnClickSave : EditShoppingListTotalEvent()

    object OnClickCancel : EditShoppingListTotalEvent()

    data class OnTotalChanged(val value: TextFieldValue) : EditShoppingListTotalEvent()

    data class OnDiscountChanged(val value: TextFieldValue) : EditShoppingListTotalEvent()

    data class OnDiscountAsPercentSelected(val asPercent: Boolean) : EditShoppingListTotalEvent()

    data class OnSelectDiscountAsPercent(val expanded: Boolean) : EditShoppingListTotalEvent()

    data class OnBudgetChanged(val value: TextFieldValue) : EditShoppingListTotalEvent()

    data class OnBudgetProductsSelected(val budgetProducts: DisplayTotal) : EditShoppingListTotalEvent()

    data class OnSelectBudgetProducts(val expanded: Boolean) : EditShoppingListTotalEvent()
}
