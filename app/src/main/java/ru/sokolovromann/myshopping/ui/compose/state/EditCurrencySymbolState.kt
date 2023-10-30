package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditCurrencySymbolState {

    private var settingsWithConfig by mutableStateOf(SettingsWithConfig())

    var screenData by mutableStateOf(EditCurrencySymbolScreenData())
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        this.settingsWithConfig = settingsWithConfig

        val userPreferences = settingsWithConfig.appConfig.userPreferences
        screenData = EditCurrencySymbolScreenData(
            screenState = ScreenState.Showing,
            symbolValue = userPreferences.currency.symbol.toTextFieldValue(),
            showSymbolError = false,
            fontSize = userPreferences.fontSize
        )
    }

    fun changeSymbolValue(symbolValue: TextFieldValue) {
        screenData = screenData.copy(
            symbolValue = symbolValue,
            showSymbolError = false
        )
    }

    fun showSymbolError() {
        screenData = screenData.copy(showSymbolError = true)
    }
}

data class EditCurrencySymbolScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val symbolValue: TextFieldValue = TextFieldValue(),
    val showSymbolError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)