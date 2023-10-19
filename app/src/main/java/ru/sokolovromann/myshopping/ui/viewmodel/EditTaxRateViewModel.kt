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
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.EditTaxRateState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditTaxRateEvent
import javax.inject.Inject

@HiltViewModel
class EditTaxRateViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val dispatchers: AppDispatchers
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
    ) = withContext(dispatchers.main) {
        val editTaxRate = AppConfigMapper.toEditTaxRate(settingsWithConfig)
        editTaxRateState.populate(editTaxRate)
        _screenEventFlow.emit(EditTaxRateScreenEvent.ShowKeyboard)
    }

    private fun saveTaxRate() = viewModelScope.launch {
        val taxRate = editTaxRateState.getTaxRateResult()
            .getOrElse { return@launch }

        appConfigRepository.saveTaxRate(taxRate)

        withContext(dispatchers.main) {
            _screenEventFlow.emit(EditTaxRateScreenEvent.ShowBackScreenAndUpdateProductsWidgets)
        }
    }

    private fun taxRateChanged(event: EditTaxRateEvent.TaxRateChanged) {
        editTaxRateState.changeTaxRateValue(event.value)
    }

    private fun cancelSavingTaxRate() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditTaxRateScreenEvent.ShowBackScreen)
    }
}