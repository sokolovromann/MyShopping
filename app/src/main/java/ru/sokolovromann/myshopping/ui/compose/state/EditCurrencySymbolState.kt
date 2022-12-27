package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.repository.model.EditCurrencySymbol
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.utils.isEmpty

class EditCurrencySymbolState {

    var screenData by mutableStateOf(EditCurrencySymbolScreenData())
        private set

    fun populate(editCurrencySymbol: EditCurrencySymbol) {
        val symbol = editCurrencySymbol.currency

        screenData = EditCurrencySymbolScreenData(
            symbolValue = TextFieldValue(
                text = symbol,
                selection = TextRange(symbol.length),
                composition = TextRange(symbol.length)
            ),
            showSymbolError = false,
            fontSize = editCurrencySymbol.preferences.fontSize
        )
    }

    fun changeSymbolValue(symbolValue: TextFieldValue) {
        screenData = screenData.copy(
            symbolValue = symbolValue,
            showSymbolError = false
        )
    }

    fun getSymbolResult(): Result<String> {
        return if (screenData.symbolValue.isEmpty()) {
            screenData = screenData.copy(showSymbolError = true)
            Result.failure(Exception())
        } else {
            Result.success(screenData.symbolValue.text)
        }
    }
}

data class EditCurrencySymbolScreenData(
    val symbolValue: TextFieldValue = TextFieldValue(),
    val showSymbolError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)