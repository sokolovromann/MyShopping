package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.EditCurrencySymbolScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.EditCurrencySymbolState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditCurrencySymbolEvent
import javax.inject.Inject

@HiltViewModel
class EditCurrencySymbolViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val dispatchers: AppDispatchers,
) : ViewModel(), ViewModelEvent<EditCurrencySymbolEvent> {

    val editCurrencySymbolState: EditCurrencySymbolState = EditCurrencySymbolState()

    private val _screenEventFlow: MutableSharedFlow<EditCurrencySymbolScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditCurrencySymbolScreenEvent> = _screenEventFlow

    init {
        getSettingsWithConfig()
    }

    override fun onEvent(event: EditCurrencySymbolEvent) {
        when (event) {
            EditCurrencySymbolEvent.SaveCurrencySymbol -> saveCurrencySymbol()

            EditCurrencySymbolEvent.CancelSavingCurrencySymbol -> cancelSavingCurrencySymbol()

            is EditCurrencySymbolEvent.CurrencySymbolChanged -> currencySymbolChanged(event)
        }
    }

    private fun getSettingsWithConfig() = viewModelScope.launch(dispatchers.io) {
        appConfigRepository.getSettingsWithConfig().firstOrNull()?.let {
            settingsWithConfigLoaded(it)
        }
    }

    private suspend fun settingsWithConfigLoaded(
        settingsWithConfig: SettingsWithConfig
    ) = withContext(dispatchers.main) {
        val editCurrencySymbol = AppConfigMapper.toEditCurrencySymbol(settingsWithConfig)
        editCurrencySymbolState.populate(editCurrencySymbol)
        _screenEventFlow.emit(EditCurrencySymbolScreenEvent.ShowKeyboard)
    }

    private fun saveCurrencySymbol() = viewModelScope.launch {
        val symbol = editCurrencySymbolState.getSymbolResult()
            .getOrElse { return@launch }

        appConfigRepository.saveCurrencySymbol(symbol)

        withContext(dispatchers.main) {
            _screenEventFlow.emit(EditCurrencySymbolScreenEvent.ShowBackScreenAndUpdateProductsWidgets)
        }
    }

    private fun cancelSavingCurrencySymbol() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditCurrencySymbolScreenEvent.ShowBackScreen)
    }

    private fun currencySymbolChanged(event: EditCurrencySymbolEvent.CurrencySymbolChanged) {
        editCurrencySymbolState.changeSymbolValue(event.value)
    }
}