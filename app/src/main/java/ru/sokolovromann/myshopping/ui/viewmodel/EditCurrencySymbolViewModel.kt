package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.EditCurrencySymbolScreenEvent
import ru.sokolovromann.myshopping.ui.model.EditCurrencySymbolState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditCurrencySymbolEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class EditCurrencySymbolViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<EditCurrencySymbolEvent> {

    val editCurrencySymbolState: EditCurrencySymbolState = EditCurrencySymbolState()

    private val _screenEventFlow: MutableSharedFlow<EditCurrencySymbolScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditCurrencySymbolScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: EditCurrencySymbolEvent) {
        when (event) {
            EditCurrencySymbolEvent.OnClickSave -> onClickSave()

            EditCurrencySymbolEvent.OnClickCancel -> onClickCancel()

            is EditCurrencySymbolEvent.OnSymbolChanged -> onSymbolChanged(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        appConfigRepository.getSettingsWithConfig().firstOrNull()?.let {
            editCurrencySymbolState.populate(it)
            _screenEventFlow.emit(EditCurrencySymbolScreenEvent.OnShowKeyboard)
        }
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        editCurrencySymbolState.onWaiting()

        appConfigRepository.saveCurrencySymbol(editCurrencySymbolState.getCurrentCurrency().symbol)
            .onSuccess { _screenEventFlow.emit(EditCurrencySymbolScreenEvent.OnShowBackScreen) }
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(EditCurrencySymbolScreenEvent.OnShowBackScreen)
    }

    private fun onSymbolChanged(event: EditCurrencySymbolEvent.OnSymbolChanged) {
        editCurrencySymbolState.onSymbolValueChanged(event.value)
    }
}