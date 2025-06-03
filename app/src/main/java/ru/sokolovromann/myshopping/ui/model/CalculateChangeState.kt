package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.utils.toBigDecimalOrZero

class CalculateChangeState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var userMoneyValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var totalText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var changeText: UiString by mutableStateOf(UiString.FromResources(R.string.calculateChange_text_noChange))
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        totalText = UiString.FromResourcesWithArgs(
            R.string.calculateChange_text_total,
            shoppingListWithConfig.getShopping().total
        )
        waiting = false
    }

    fun onUserMoneyValueChanged(value: TextFieldValue) {
        userMoneyValue = value

        val change = shoppingListWithConfig.calculateChange(value.toBigDecimalOrZero())
        changeText = if (change.isEmpty()) {
            UiString.FromResources(R.string.calculateChange_text_noChange)
        } else {
            UiString.FromResourcesWithArgs(R.string.calculateChange_text_change, change.getDisplayValue())
        }
    }

    fun onWaiting() {
        waiting = true
    }
}