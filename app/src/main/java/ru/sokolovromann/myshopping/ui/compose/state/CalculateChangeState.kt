package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero

class CalculateChangeState {

    private var calculateChange by mutableStateOf(CalculateChange())

    var screenData by mutableStateOf(CalculateChangeScreenData())
        private set

    fun populate(calculateChange: CalculateChange) {
        this.calculateChange = calculateChange

        val totalText: UiText = UiText.FromResourcesWithArgs(
            R.string.calculateChange_text_total,
            calculateChange.calculateTotal().toString()
        )

        val changeText: UiText = UiText.FromResources(R.string.calculateChange_text_noChange)

        screenData = CalculateChangeScreenData(
            screenState = ScreenState.Showing,
            userMoneyValue = TextFieldValue(),
            totalText = totalText,
            changeText = changeText,
            fontSize = calculateChange.preferences.fontSize
        )
    }

    fun changeUserMoneyValue(userMoneyValue: TextFieldValue) {
        val change: Float = userMoneyValue.toFloatOrZero() - calculateChange.calculateTotal().value
        val changeMoney = Money(
            value = change,
            currency = calculateChange.preferences.currency
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
    val screenState: ScreenState = ScreenState.Nothing,
    val userMoneyValue: TextFieldValue = TextFieldValue(),
    val totalText: UiText = UiText.Nothing,
    val changeText: UiText = UiText.Nothing,
    val fontSize: FontSize = FontSize.MEDIUM
)