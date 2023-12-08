package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.EditCurrencySymbolScreenEvent
import ru.sokolovromann.myshopping.ui.model.EditCurrencySymbolState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditCurrencySymbolEvent
import javax.inject.Inject

@HiltViewModel
class EditCurrencySymbolViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
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

    private fun getSettingsWithConfig() = viewModelScope.launch {
        appConfigRepository.getSettingsWithConfig().firstOrNull()?.let {
            settingsWithConfigLoaded(it)
        }
    }

    private suspend fun settingsWithConfigLoaded(
        settingsWithConfig: SettingsWithConfig
    ) = withContext(AppDispatchers.Main) {
        editCurrencySymbolState.populate(settingsWithConfig)
        _screenEventFlow.emit(EditCurrencySymbolScreenEvent.ShowKeyboard)
    }

    private fun saveCurrencySymbol() = viewModelScope.launch {
        editCurrencySymbolState.onWaiting()

        appConfigRepository.saveCurrencySymbol(editCurrencySymbolState.getCurrentCurrency().symbol)
            .onSuccess {
                withContext(AppDispatchers.Main) {
                    _screenEventFlow.emit(EditCurrencySymbolScreenEvent.ShowBackScreenAndUpdateProductsWidgets)
                }
            }
    }

    private fun cancelSavingCurrencySymbol() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(EditCurrencySymbolScreenEvent.ShowBackScreen)
    }

    private fun currencySymbolChanged(event: EditCurrencySymbolEvent.CurrencySymbolChanged) {
        editCurrencySymbolState.onSymbolValueChanged(event.value)
    }
}