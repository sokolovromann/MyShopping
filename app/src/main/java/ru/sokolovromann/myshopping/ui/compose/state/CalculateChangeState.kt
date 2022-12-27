package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero

class CalculateChangeState {

    private var shoppingList by mutableStateOf(ShoppingList())

    var screenData by mutableStateOf(CalculateChangeScreenData())
        private set

    fun populate(calculateChange: CalculateChange) {
        shoppingList = calculateChange.shoppingList ?: ShoppingList()

        val totalText: UiText = UiText.FromResourcesWithArgs(
            R.string.calculateChange_text_total,
            shoppingList.calculateTotal().toString()
        )

        val changeText: UiText = UiText.FromResources(R.string.calculateChange_text_noChange)

        screenData = CalculateChangeScreenData(
            userMoneyValue = TextFieldValue(),
            totalText = totalText,
            changeText = changeText,
            fontSize = calculateChange.preferences.fontSize
        )
    }

    fun changeUserMoneyValue(userMoneyValue: TextFieldValue) {
        val change: Float = userMoneyValue.toFloatOrZero() - shoppingList.calculateTotal().value
        val changeMoney = Money(
            value = change,
            currency = shoppingList.currency
        )
        val changeText: UiText = if (changeMoney.isEmpty()) {
            UiText.FromResources(R.string.calculateChange_text_noChange)
        } else {
            UiText.FromResourcesWithArgs(R.string.calculateChange_text_change, changeMoney)
        }

        screenData = screenData.copy(
            userMoneyValue = userMoneyValue,
            changeText = changeText
        )
    }
}

data class CalculateChangeScreenData(
    val userMoneyValue: TextFieldValue = TextFieldValue(),
    val totalText: UiText = UiText.Nothing,
    val changeText: UiText = UiText.Nothing,
    val fontSize: FontSize = FontSize.MEDIUM
)