package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.repository.model.EditCurrencySymbol
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditCurrencySymbolState {

    private var editCurrencySymbol by mutableStateOf(EditCurrencySymbol())

    var screenData by mutableStateOf(EditCurrencySymbolScreenData())
        private set

    fun populate(editCurrencySymbol: EditCurrencySymbol) {
        this.editCurrencySymbol = editCurrencySymbol

        screenData = EditCurrencySymbolScreenData(
            screenState = ScreenState.Showing,
            symbolValue = editCurrencySymbol.getFieldSymbol().toTextFieldValue(),
            showSymbolError = false,
            fontSize = editCurrencySymbol.getFontSize()
        )
    }

    fun changeSymbolValue(symbolValue: TextFieldValue) {
        screenData = screenData.copy(
            symbolValue = symbolValue,
            showSymbolError = false
        )
    }

    fun getSymbolResult(): Result<String> {
        screenData = screenData.copy(screenState = ScreenState.Saving)

        val symbol = editCurrencySymbol.createSymbol(
            symbol = screenData.symbolValue.text
        ).getOrNull()

        return if (symbol == null) {
            screenData = screenData.copy(showSymbolError = true)
            Result.failure(Exception())
        } else {
            screenData = screenData.copy(screenState = ScreenState.Saving)
            Result.success(symbol)
        }
    }
}

data class EditCurrencySymbolScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val symbolValue: TextFieldValue = TextFieldValue(),
    val showSymbolError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)