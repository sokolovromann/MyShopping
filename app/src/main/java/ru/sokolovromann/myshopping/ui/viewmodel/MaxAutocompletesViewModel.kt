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
import ru.sokolovromann.myshopping.ui.compose.event.MaxAutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.MaxAutocompletesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.MaxAutocompletesEvent
import javax.inject.Inject

@HiltViewModel
class MaxAutocompletesViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<MaxAutocompletesEvent> {

    val maxAutocompletesState = MaxAutocompletesState()

    private val _screenEventFlow: MutableSharedFlow<MaxAutocompletesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MaxAutocompletesScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: MaxAutocompletesEvent) {
        when (event) {
            MaxAutocompletesEvent.OnClickSave -> onClickSave()

            MaxAutocompletesEvent.OnClickCancel -> onClickCancel()

            MaxAutocompletesEvent.OnClickPlusOneName -> onClickPlusOneName()

            MaxAutocompletesEvent.OnClickMinusOneName -> onClickMinusOneName()

            MaxAutocompletesEvent.OnClickPlusOneQuantity -> onClickPlusOneQuantity()

            MaxAutocompletesEvent.OnClickMinusOneQuantity -> onClickMinusOneQuantity()

            MaxAutocompletesEvent.OnClickPlusOneMoney -> onClickPlusOneMoney()

            MaxAutocompletesEvent.OnClickMinusOneMoney -> onClickMinusOneMoney()

            MaxAutocompletesEvent.OnClickPlusOneOther -> onClickPlusOneOther()

            MaxAutocompletesEvent.OnClickMinusOneOther -> onClickMinusOneOther()
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.getSettingsWithConfig().firstOrNull()?.let {
            maxAutocompletesState.populate(it)
        }
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        maxAutocompletesState.onWaiting()

        appConfigRepository.saveMaxAutocompletes(
            maxNames = maxAutocompletesState.maxNames,
            maxQuantities = maxAutocompletesState.maxQuantities,
            maxMoneys = maxAutocompletesState.maxMoneys,
            maxOthers = maxAutocompletesState.maxOthers
        ).onSuccess { _screenEventFlow.emit(MaxAutocompletesScreenEvent.OnShowBackScreen) }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(MaxAutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onClickPlusOneName() {
        val value = plusOneValue(maxAutocompletesState.maxNames)
        maxAutocompletesState.onMaxNamesChanged(value)
    }

    private fun onClickMinusOneName() {
        val value = minusOneValue(maxAutocompletesState.maxNames)
        maxAutocompletesState.onMaxNamesChanged(value)
    }

    private fun onClickPlusOneQuantity() {
        val value = plusOneValue(maxAutocompletesState.maxQuantities)
        maxAutocompletesState.onMaxQuantitiesChanged(value)
    }

    private fun onClickMinusOneQuantity() {
        val value = minusOneValue(maxAutocompletesState.maxQuantities)
        maxAutocompletesState.onMaxQuantitiesChanged(value)
    }

    private fun onClickPlusOneMoney() {
        val value = plusOneValue(maxAutocompletesState.maxMoneys)
        maxAutocompletesState.onMaxMoneysChanged(value)
    }

    private fun onClickMinusOneMoney() {
        val value = minusOneValue(maxAutocompletesState.maxMoneys)
        maxAutocompletesState.onMaxMoneysChanged(value)
    }

    private fun onClickPlusOneOther() {
        val value = plusOneValue(maxAutocompletesState.maxOthers)
        maxAutocompletesState.onMaxOthersChanged(value)
    }

    private fun onClickMinusOneOther() {
        val value = minusOneValue(maxAutocompletesState.maxOthers)
        maxAutocompletesState.onMaxOthersChanged(value)
    }

    private fun plusOneValue(value: Int): Int {
        return value.plus(1)
    }

    private fun minusOneValue(value: Int): Int {
        val defaultValue = 0
        val calculatedValue = value.minus(1)
        return if (calculatedValue < 0) defaultValue else calculatedValue
    }
}