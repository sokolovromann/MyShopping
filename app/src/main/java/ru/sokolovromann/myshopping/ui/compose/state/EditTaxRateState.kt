package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero

class EditTaxRateState {

    private var editTaxRate by mutableStateOf(EditTaxRate())

    var screenData by mutableStateOf(EditTaxRateScreenData())
        private set

    fun populate(editTaxRate: EditTaxRate) {
        this.editTaxRate = editTaxRate
        val preferences = editTaxRate.appConfig.userPreferences

        val taxRateText = preferences.taxRate.getFormattedValueWithoutSeparators()

        screenData = EditTaxRateScreenData(
            screenState = ScreenState.Showing,
            taxRateValue = TextFieldValue(
                text = taxRateText,
                selection = TextRange(taxRateText.length),
                composition = TextRange(taxRateText.length)
            ),
            showTaxRateError = false,
            fontSize = preferences.fontSize
        )
    }

    fun changeTaxRateValue(taxRateValue: TextFieldValue) {
        screenData = screenData.copy(
            taxRateValue = taxRateValue,
            showTaxRateError = false
        )
    }

    fun getTaxRateResult(): Result<Money> {
        return if (screenData.taxRateValue.isEmpty()) {
            screenData = screenData.copy(showTaxRateError = true)
            Result.failure(Exception())
        } else {
            screenData = screenData.copy(screenState = ScreenState.Saving)
            val success = editTaxRate.appConfig.userPreferences.taxRate.copy(
                value = screenData.taxRateValue.toFloatOrZero()
            )
            Result.success(success)
        }
    }
}

data class EditTaxRateScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val taxRateValue: TextFieldValue = TextFieldValue(),
    val showTaxRateError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)