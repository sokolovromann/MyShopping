package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.TaxRate
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero

class EditTaxRateState {

    private var taxRate by mutableStateOf(TaxRate())

    var screenData by mutableStateOf(EditTaxRateScreenData())
        private set

    fun populate(editTaxRate: EditTaxRate) {
        taxRate = editTaxRate.taxRate

        val taxRateText = taxRate.valueToString()

        screenData = EditTaxRateScreenData(
            taxRateValue = TextFieldValue(
                text = taxRateText,
                selection = TextRange(taxRateText.length),
                composition = TextRange(taxRateText.length)
            ),
            showTaxRateError = false,
            fontSize = editTaxRate.preferences.fontSize
        )
    }

    fun changeTaxRateValue(taxRateValue: TextFieldValue) {
        screenData = screenData.copy(
            taxRateValue = taxRateValue,
            showTaxRateError = false
        )
    }

    fun getTaxRateResult(): Result<TaxRate> {
        return if (screenData.taxRateValue.isEmpty()) {
            screenData = screenData.copy(showTaxRateError = true)
            Result.failure(Exception())
        } else {
            val success = taxRate.copy(
                value = screenData.taxRateValue.toFloatOrZero()
            )
            Result.success(success)
        }
    }
}

data class EditTaxRateScreenData(
    val taxRateValue: TextFieldValue = TextFieldValue(),
    val showTaxRateError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)