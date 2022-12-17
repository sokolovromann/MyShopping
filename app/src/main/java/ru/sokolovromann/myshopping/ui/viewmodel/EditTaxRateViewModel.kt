package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.EditTaxRateRepository
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.SettingsPreferences
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.TextFieldState
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditTaxRateEvent
import javax.inject.Inject

@HiltViewModel
class EditTaxRateViewModel @Inject constructor(
    private val repository: EditTaxRateRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
) : ViewModel(), ViewModelEvent<EditTaxRateEvent> {

    private val editTaxRateState: MutableState<EditTaxRate> = mutableStateOf(EditTaxRate())

    private val _headerState: MutableState<TextData> = mutableStateOf(TextData())
    val headerState: State<TextData> = _headerState

    val taxRateState: TextFieldState = TextFieldState()

    private val _cancelState: MutableState<TextData> = mutableStateOf(TextData())
    val cancelState: State<TextData> = _cancelState

    private val _saveState: MutableState<TextData> = mutableStateOf(TextData())
    val saveState: State<TextData> = _saveState

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

    private val _screenEventFlow: MutableSharedFlow<EditTaxRateScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditTaxRateScreenEvent> = _screenEventFlow

    init {
        showCancelButton()
        showSaveButton()
        getTaxRate()
    }

    override fun onEvent(event: EditTaxRateEvent) {
        when (event) {
            EditTaxRateEvent.SaveTaxRate -> saveTaxRate()

            EditTaxRateEvent.CancelSavingTaxRate -> cancelSavingTaxRate()

            is EditTaxRateEvent.TaxRateChanged -> taxRateChanged(event)
        }
    }

    private fun getTaxRate() = viewModelScope.launch(dispatchers.io) {
        repository.getEditTaxRate().firstOrNull()?.let {
            showTaxRate(it)
        }
    }

    private fun saveTaxRate() = viewModelScope.launch(dispatchers.io) {
        if (taxRateState.isTextEmpty()) {
            taxRateState.showError(
                error = mapping.toBody(
                    text = mapping.toResourcesUiText(R.string.editTaxRate_taxRateError),
                    fontSize = FontSize.MEDIUM,
                    appColor = AppColor.Error
                )
            )
            return@launch
        }

        val taxRate = editTaxRateState.value.taxRate.copy(
            value = mapping.toFloat(taxRateState.currentData.text) ?: 0f
        )
        repository.editTaxRate(taxRate)

        hideKeyboard()
        showBackScreen()
    }

    private fun showHeader(preferences: SettingsPreferences) {
        _headerState.value = mapping.toOnDialogHeader(
            text = mapping.toResourcesUiText(R.string.editTaxRate_header),
            fontSize = preferences.fontSize
        )
    }

    private fun showCancelButton() {
        _cancelState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editTaxRate_action_cancelSavingTaxRate),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showSaveButton() {
        _saveState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editTaxRate_action_saveTaxRate),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun taxRateChanged(event: EditTaxRateEvent.TaxRateChanged) {
        taxRateState.changeText(event.value)
    }

    private fun cancelSavingTaxRate() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditTaxRateScreenEvent.ShowBackScreen)
    }

    private suspend fun showTaxRate(
        editTaxRate: EditTaxRate
    ) = withContext(dispatchers.main) {
        editTaxRateState.value = editTaxRate

        val taxRate = editTaxRate.taxRate.valueToString()
        val preferences = editTaxRate.preferences

        taxRateState.showTextField(
            text = TextFieldValue(
                text = taxRate,
                selection = TextRange(taxRate.length),
                composition = TextRange(taxRate.length)
            ),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.editTaxRate_taxRateLabel),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                capitalization = KeyboardCapitalization.None
            )
        )

        showHeader(preferences)
        showKeyboard()
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(true)
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }
}