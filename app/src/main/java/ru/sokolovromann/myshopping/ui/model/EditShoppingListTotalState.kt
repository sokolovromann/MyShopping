package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListTotalState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var header: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var totalValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var discountValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var discountAsPercentValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
        private set

    var expandedDiscountAsPercent: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.getShopping()
        header = if (shopping.totalFormatted) {
            UiString.FromResources(R.string.editShoppingListTotal_header_editShoppingListTotal)
        } else {
            UiString.FromResources(R.string.editShoppingListTotal_header_addShoppingListTotal)
        }
        totalValue = shopping.getTotalWithoutDiscount().toTextFieldValue()
        discountValue = shopping.discount.toTextFieldValue()
        discountAsPercentValue = toDiscountSelectedValue(shopping.discount.asPercent)
        expandedDiscountAsPercent = false
        waiting = false
    }

    fun onTotalValueChanged(value: TextFieldValue) {
        totalValue = value
        waiting = false
    }

    fun onDiscountValueChanged(value: TextFieldValue) {
        discountValue = value
        waiting = false
    }

    fun onDiscountAsPercentSelected(asPercent: Boolean) {
        discountAsPercentValue = toDiscountSelectedValue(asPercent)
        expandedDiscountAsPercent = false
    }

    fun onSelectDiscountAsPercent(expanded: Boolean) {
        expandedDiscountAsPercent = expanded
    }

    fun onWaiting() {
        waiting = true
    }

    fun getCurrentShopping(): Shopping {
        val total = shoppingListWithConfig.getShopping().total.copy(
            value = totalValue.toFloatOrZero()
        )
        val discount = shoppingListWithConfig.getShopping().discount.copy(
            value = discountValue.toFloatOrZero(),
            asPercent = discountAsPercentValue.selected
        )
        return shoppingListWithConfig.getShopping().copy(
            total = total,
            totalFormatted = total.isNotEmpty(),
            discount = discount,
            discountProducts = DisplayTotal.ALL,
            lastModified = DateTime.getCurrentDateTime()
        )
    }

    private fun toDiscountSelectedValue(asPercent: Boolean): SelectedValue<Boolean> {
        return SelectedValue(
            selected = asPercent,
            text = if (asPercent) {
                UiString.FromResources(R.string.editShoppingListTotal_action_selectDiscountAsPercents)
            } else {
                UiString.FromResources(R.string.editShoppingListTotal_action_selectDiscountAsMoney)
            }
        )
    }
}