package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
import ru.sokolovromann.myshopping.ui.model.EditTaxRateState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditTaxRateEvent
import javax.inject.Inject

@HiltViewModel
class EditTaxRateViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<EditTaxRateEvent> {

    val editTaxRateState: EditTaxRateState = EditTaxRateState()

    private val _screenEventFlow: MutableSharedFlow<EditTaxRateScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditTaxRateScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: EditTaxRateEvent) {
        when (event) {
            EditTaxRateEvent.OnClickSave -> onClickSave()

            EditTaxRateEvent.OnClickCancel -> onClickCancel()

            is EditTaxRateEvent.OnTaxRateChanged -> onTaxRateChanged(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.getSettingsWithConfig().firstOrNull()?.let {
            editTaxRateState.populate(it)
            _screenEventFlow.emit(EditTaxRateScreenEvent.OnShowKeyboard)
        }
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        editTaxRateState.onWaiting()

        appConfigRepository.saveTaxRate(editTaxRateState.getCurrentTaxRate())
            .onSuccess { _screenEventFlow.emit(EditTaxRateScreenEvent.OnShowBackScreen) }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(EditTaxRateScreenEvent.OnShowBackScreen)
    }

    private fun onTaxRateChanged(event: EditTaxRateEvent.OnTaxRateChanged) {
        editTaxRateState.onTaxRateValueChanged(event.value)
    }
}