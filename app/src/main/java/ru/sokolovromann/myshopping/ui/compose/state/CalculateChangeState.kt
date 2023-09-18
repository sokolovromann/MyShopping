package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.utils.toFloatOrNull

class CalculateChangeState {

    private var calculateChange by mutableStateOf(CalculateChange())

    var screenData by mutableStateOf(CalculateChangeScreenData())
        private set

    fun populate(calculateChange: CalculateChange) {
        this.calculateChange = calculateChange

        val totalText: UiText = UiText.FromResourcesWithArgs(
            R.string.calculateChange_text_total,
            calculateChange.getDisplayTotal()
        )

        val changeText: UiText = UiText.FromResources(R.string.calculateChange_text_noChange)

        screenData = CalculateChangeScreenData(
            screenState = ScreenState.Showing,
            userMoneyValue = TextFieldValue(),
            totalText = totalText,
            changeText = changeText,
            fontSize = calculateChange.getFontSize()
        )
    }

    fun changeUserMoneyValue(userMoneyValue: TextFieldValue) {
        val change = calculateChange.calculateChange(
            userMoney = userMoneyValue.toFloatOrNull()
        )

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
}

data class CalculateChangeScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val userMoneyValue: TextFieldValue = TextFieldValue(),
    val totalText: UiText = UiText.Nothing,
    val changeText: UiText = UiText.Nothing,
    val fontSize: FontSize = FontSize.MEDIUM
)