package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetailsInfo
import ru.sokolovromann.myshopping.manager.SuggestionsManager
import ru.sokolovromann.myshopping.ui.compose.event.MaxAutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.MaxAutocompletesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.MaxAutocompletesEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class MaxAutocompletesViewModel @Inject constructor(
    private val suggestionsManager: SuggestionsManager
) : ViewModel(), ViewModelEvent<MaxAutocompletesEvent> {

    val maxAutocompletesState = MaxAutocompletesState()

    private val _screenEventFlow: MutableSharedFlow<MaxAutocompletesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MaxAutocompletesScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: MaxAutocompletesEvent) {
        when (event) {
            MaxAutocompletesEvent.OnClickSave -> onClickSave()

            MaxAutocompletesEvent.OnClickCancel -> onClickCancel()

            is MaxAutocompletesEvent.OnSelectTakeNames -> onSelectTakeNames(event)

            is MaxAutocompletesEvent.OnSelectTakeDetailsDescriptions -> onSelectTakeDetailsDescriptions(event)

            is MaxAutocompletesEvent.OnSelectTakeDetailsQuantities -> onSelectTakeDetailsQuantities(event)

            is MaxAutocompletesEvent.OnSelectTakeDetailsMoney -> onSelectTakeDetailsMoney(event)

            is MaxAutocompletesEvent.OnTakeNamesSelected -> onTakeNamesSelected(event)

            is MaxAutocompletesEvent.OnTakeDetailsDescriptionsSelected -> onTakeDetailsDescriptionsSelected(event)

            is MaxAutocompletesEvent.OnTakeDetailsQuantitiesSelected -> onTakeDetailsQuantitiesSelected(event)

            is MaxAutocompletesEvent.OnTakeDetailsMoneySelected -> onTakeDetailsMoneySelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        val config = suggestionsManager.getConfig()
        maxAutocompletesState.populate(config)
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        maxAutocompletesState.onWaiting()

        suggestionsManager.updateConfig(maxAutocompletesState.takeNamesValue.selected)

        val takeSuggestionDetailsInfo = TakeSuggestionDetailsInfo(
            descriptions = maxAutocompletesState.takeDetailsDescriptions.selected,
            quantities = maxAutocompletesState.takeDetailsQuantities.selected,
            money = maxAutocompletesState.takeDetailsMoney.selected
        )
        suggestionsManager.updateConfig(takeSuggestionDetailsInfo)

        _screenEventFlow.emit(MaxAutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(MaxAutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onSelectTakeNames(event: MaxAutocompletesEvent.OnSelectTakeNames) {
        maxAutocompletesState.onSelectTakeNames(event.expanded)
    }

    private fun onSelectTakeDetailsDescriptions(event: MaxAutocompletesEvent.OnSelectTakeDetailsDescriptions) {
        maxAutocompletesState.onSelectTakeDetailsDescriptions(event.expanded)
    }

    private fun onSelectTakeDetailsQuantities(event: MaxAutocompletesEvent.OnSelectTakeDetailsQuantities) {
        maxAutocompletesState.onSelectTakeDetailsQuantities(event.expanded)
    }

    private fun onSelectTakeDetailsMoney(event: MaxAutocompletesEvent.OnSelectTakeDetailsMoney) {
        maxAutocompletesState.onSelectTakeDetailsMoney(event.expanded)
    }

    private fun onTakeNamesSelected(event: MaxAutocompletesEvent.OnTakeNamesSelected) {
        maxAutocompletesState.onTakeNamesSelected(event.takeSuggestions)
    }

    private fun onTakeDetailsDescriptionsSelected(event: MaxAutocompletesEvent.OnTakeDetailsDescriptionsSelected) {
        maxAutocompletesState.onTakeDetailsDescriptionsSelected(event.takeSuggestionDetails)
    }

    private fun onTakeDetailsQuantitiesSelected(event: MaxAutocompletesEvent.OnTakeDetailsQuantitiesSelected) {
        maxAutocompletesState.onTakeDetailsQuantitiesSelected(event.takeSuggestionDetails)
    }

    private fun onTakeDetailsMoneySelected(event: MaxAutocompletesEvent.OnTakeDetailsMoneySelected) {
        maxAutocompletesState.onTakeDetailsMoneySelected(event.takeSuggestionDetails)
    }
}