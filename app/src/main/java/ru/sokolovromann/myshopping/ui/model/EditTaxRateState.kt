package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditTaxRateState {

    private var settingsWithConfig by mutableStateOf(SettingsWithConfig())

    var taxRateValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        this.settingsWithConfig = settingsWithConfig

        val userPreferences = settingsWithConfig.appConfig.userPreferences
        taxRateValue = userPreferences.taxRate.toTextFieldValue(displayZeroIfEmpty = true)
        waiting = false
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
    }

    fun onTaxRateValueChanged(value: TextFieldValue) {
        taxRateValue = value
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun getCurrentTaxRate(): Money {
        return settingsWithConfig.appConfig.userPreferences.taxRate.copy(
            value = taxRateValue.toFloatOrZero()
        )
    }
}