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
import ru.sokolovromann.myshopping.data.repository.EditCurrencySymbolRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.event.EditCurrencySymbolScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.TextFieldState
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditCurrencySymbolEvent
import javax.inject.Inject

@HiltViewModel
class EditCurrencySymbolViewModel @Inject constructor(
    private val repository: EditCurrencySymbolRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
) : ViewModel(), ViewModelEvent<EditCurrencySymbolEvent> {

    private val editCurrencySymbolState: MutableState<EditCurrencySymbol> = mutableStateOf(EditCurrencySymbol())

    private val _headerState: MutableState<TextData> = mutableStateOf(TextData())
    val headerState: State<TextData> = _headerState

    val symbolState: TextFieldState = TextFieldState()

    private val _cancelState: MutableState<TextData> = mutableStateOf(TextData())
    val cancelState: State<TextData> = _cancelState

    private val _saveState: MutableState<TextData> = mutableStateOf(TextData())
    val saveState: State<TextData> = _saveState

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

    private val _screenEventFlow: MutableSharedFlow<EditCurrencySymbolScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditCurrencySymbolScreenEvent> = _screenEventFlow

    init {
        showCancelButton()
        showSaveButton()
        getCurrencySymbol()
    }

    override fun onEvent(event: EditCurrencySymbolEvent) {
        when (event) {
            EditCurrencySymbolEvent.SaveCurrencySymbol -> saveCurrencySymbol()

            EditCurrencySymbolEvent.CancelSavingCurrencySymbol -> cancelSavingCurrencySymbol()

            is EditCurrencySymbolEvent.CurrencySymbolChanged -> currencySymbolChanged(event)
        }
    }

    private fun getCurrencySymbol() = viewModelScope.launch(dispatchers.io) {
        repository.getEditCurrencySymbol().firstOrNull()?.let {
            showCurrencySymbol(it)
        }
    }

    private fun saveCurrencySymbol() = viewModelScope.launch(dispatchers.io) {
        if (symbolState.isTextEmpty()) {
            symbolState.showError(
                error = mapping.toBody(
                    text = mapping.toResourcesUiText(R.string.editCurrencySymbol_message_symbolError),
                    fontSize = FontSize.MEDIUM,
                    appColor = AppColor.Error
                )
            )
            return@launch
        }

        val symbol = symbolState.currentData.text.text
        repository.editCurrencySymbol(symbol)

        hideKeyboard()
        showBackScreen()
    }

    private fun cancelSavingCurrencySymbol() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun currencySymbolChanged(event: EditCurrencySymbolEvent.CurrencySymbolChanged) {
        symbolState.changeText(event.value)
    }

    private fun showHeader(preferences: SettingsPreferences) {
        _headerState.value = mapping.toOnDialogHeader(
            text = mapping.toResourcesUiText(R.string.editCurrencySymbol_header),
            fontSize = preferences.fontSize
        )
    }

    private fun showCancelButton() {
        _cancelState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editCurrencySymbol_action_cancelSavingCurrencySymbol),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showSaveButton() {
        _saveState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editCurrencySymbol_action_saveCurrencySymbol),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditCurrencySymbolScreenEvent.ShowBackScreen)
    }

    private suspend fun showCurrencySymbol(
        editCurrencySymbol: EditCurrencySymbol
    ) = withContext(dispatchers.main) {
        editCurrencySymbolState.value = editCurrencySymbol

        val currency = editCurrencySymbol.currency
        val preferences = editCurrencySymbol.preferences

        symbolState.showTextField(
            text = TextFieldValue(
                text = currency,
                selection = TextRange(currency.length),
                composition = TextRange(currency.length)
            ),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.editCurrencySymbol_label_symbol),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
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