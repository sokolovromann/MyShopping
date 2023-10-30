package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditTaxRateState {

    private var settingsWithConfig by mutableStateOf(SettingsWithConfig())

    var screenData by mutableStateOf(EditTaxRateScreenData())
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        this.settingsWithConfig = settingsWithConfig

        val userPreferences = settingsWithConfig.appConfig.userPreferences
        screenData = EditTaxRateScreenData(
            screenState = ScreenState.Showing,
            taxRateValue = userPreferences.taxRate.getFormattedValueWithoutSeparators().toTextFieldValue(),
            showTaxRateError = false,
            fontSize = userPreferences.fontSize
        )
    }

    fun changeTaxRateValue(taxRateValue: TextFieldValue) {
        screenData = screenData.copy(
            taxRateValue = taxRateValue,
            showTaxRateError = false
        )
    }

    fun getTaxRate(): Money {
        return settingsWithConfig.appConfig.userPreferences.taxRate.copy(
            value = screenData.taxRateValue.toFloatOrZero()
        )
    }
}

data class EditTaxRateScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val taxRateValue: TextFieldValue = TextFieldValue(),
    val showTaxRateError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)