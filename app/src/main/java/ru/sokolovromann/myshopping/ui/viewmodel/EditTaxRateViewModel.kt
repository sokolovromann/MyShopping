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

    init {
        getSettingsWithConfig()
    }

    override fun onEvent(event: EditTaxRateEvent) {
        when (event) {
            EditTaxRateEvent.SaveTaxRate -> saveTaxRate()

            EditTaxRateEvent.CancelSavingTaxRate -> cancelSavingTaxRate()

            is EditTaxRateEvent.TaxRateChanged -> taxRateChanged(event)
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
        editTaxRateState.populate(settingsWithConfig)
        _screenEventFlow.emit(EditTaxRateScreenEvent.ShowKeyboard)
    }

    private fun saveTaxRate() = viewModelScope.launch {
        editTaxRateState.onWaiting()

        appConfigRepository.saveTaxRate(editTaxRateState.getCurrentTaxRate())

        withContext(AppDispatchers.Main) {
            _screenEventFlow.emit(EditTaxRateScreenEvent.ShowBackScreenAndUpdateProductsWidgets)
        }
    }

    private fun taxRateChanged(event: EditTaxRateEvent.TaxRateChanged) {
        editTaxRateState.onTaxRateValueChanged(event.value)
    }

    private fun cancelSavingTaxRate() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(EditTaxRateScreenEvent.ShowBackScreen)
    }
}