package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.ui.utils.toFloatOrNull
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditTaxRateState {

    private var editTaxRate by mutableStateOf(EditTaxRate())

    var screenData by mutableStateOf(EditTaxRateScreenData())
        private set

    fun populate(editTaxRate: EditTaxRate) {
        this.editTaxRate = editTaxRate

        screenData = EditTaxRateScreenData(
            screenState = ScreenState.Showing,
            taxRateValue = editTaxRate.getFieldTaxRate().toTextFieldValue(),
            showTaxRateError = false,
            fontSize = editTaxRate.getFontSize()
        )
    }

    fun changeTaxRateValue(taxRateValue: TextFieldValue) {
        screenData = screenData.copy(
            taxRateValue = taxRateValue,
            showTaxRateError = false
        )
    }

    fun getTaxRateResult(): Result<Money> {
        val taxRate = editTaxRate.createTaxRate(
            taxRate = screenData.taxRateValue.toFloatOrNull()
        ).getOrNull()

        return if (taxRate == null) {
            screenData = screenData.copy(showTaxRateError = true)
            Result.failure(Exception())
        } else {
            Result.success(taxRate)
        }
    }
}

data class EditTaxRateScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val taxRateValue: TextFieldValue = TextFieldValue(),
    val showTaxRateError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)