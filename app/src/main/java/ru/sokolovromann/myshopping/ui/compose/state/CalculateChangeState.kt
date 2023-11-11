package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.utils.toFloatOrNull

class CalculateChangeState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var screenData by mutableStateOf(CalculateChangeScreenData())
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val totalText: UiText = UiText.FromResourcesWithArgs(
            R.string.calculateChange_text_total,
            shoppingListWithConfig.getShopping().total
        )

        val changeText: UiText = UiText.FromResources(R.string.calculateChange_text_noChange)

        screenData = CalculateChangeScreenData(
            screenState = ScreenState.Showing,
            userMoneyValue = TextFieldValue(),
            totalText = totalText,
            changeText = changeText,
            fontSize = shoppingListWithConfig.getUserPreferences().fontSize
        )
    }

    fun changeUserMoneyValue(userMoneyValue: TextFieldValue) {
        val change = calculateChange(userMoneyValue.toFloatOrNull())

        val changeText: UiText = if (change.isEmpty()) {
            UiText.FromResources(R.string.calculateChange_text_noChange)
        } else {
            UiText.FromResourcesWithArgs(R.string.calculateChange_text_change, change)
        }

        screenData = screenData.copy(
            userMoneyValue = userMoneyValue,
            changeText = changeText
        )
    }

    private fun calculateChange(userMoney: Float?): String {
        val total = shoppingListWithConfig.getShopping().total
        return if (userMoney == null || userMoney <= total.value) {
            ""
        } else {
            val value = userMoney - total.value
            Money(
                value = value,
                currency = total.currency,
                decimalFormat = total.decimalFormat
            ).getDisplayValue()
        }
    }
}

data class CalculateChangeScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val userMoneyValue: TextFieldValue = TextFieldValue(),
    val totalText: UiText = UiText.Nothing,
    val changeText: UiText = UiText.Nothing,
    val fontSize: FontSize = FontSize.MEDIUM
)