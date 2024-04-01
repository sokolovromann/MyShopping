package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditCurrencySymbolState {

    private var settingsWithConfig: SettingsWithConfig by mutableStateOf(SettingsWithConfig())

    var symbolValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        this.settingsWithConfig = settingsWithConfig

        val userPreferences = settingsWithConfig.appConfig.userPreferences
        symbolValue = userPreferences.currency.symbol.toTextFieldValue()
        waiting = false
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.appFontSize)
    }

    fun onSymbolValueChanged(value: TextFieldValue) {
        symbolValue = value
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun getCurrentCurrency(): Currency {
        return settingsWithConfig.appConfig.userPreferences.currency.copy(
            symbol = symbolValue.text
        )
    }
}