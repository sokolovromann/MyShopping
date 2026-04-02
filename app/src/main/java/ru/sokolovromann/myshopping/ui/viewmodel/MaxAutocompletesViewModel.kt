package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

            is MaxAutocompletesEvent.OnSelectTakeSuggestions -> onSelectTakeSuggestions(event)

            is MaxAutocompletesEvent.OnSelectTakeDetails -> onSelectTakeDetails(event)

            is MaxAutocompletesEvent.OnTakeSuggestionsSelected -> onTakeSuggestionsSelected(event)

            is MaxAutocompletesEvent.OnTakeDetailsSelected -> onTakeDetailsSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        val config = suggestionsManager.getConfig()
        maxAutocompletesState.populate(config)
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        maxAutocompletesState.onWaiting()
        suggestionsManager.apply {
            updateConfig(maxAutocompletesState.takeSuggestionsValue.selected)
            updateConfig(maxAutocompletesState.takeDetailsValue.selected)
        }

        _screenEventFlow.emit(MaxAutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(MaxAutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onSelectTakeSuggestions(event: MaxAutocompletesEvent.OnSelectTakeSuggestions) {
        maxAutocompletesState.onSelectTakeSuggestions(event.expanded)
    }

    private fun onSelectTakeDetails(event: MaxAutocompletesEvent.OnSelectTakeDetails) {
        maxAutocompletesState.onSelectTakeDetails(event.expanded)
    }

    private fun onTakeSuggestionsSelected(event: MaxAutocompletesEvent.OnTakeSuggestionsSelected) {
        maxAutocompletesState.onTakeSuggestionsSelected(event.takeSuggestions)
    }

    private fun onTakeDetailsSelected(event: MaxAutocompletesEvent.OnTakeDetailsSelected) {
        maxAutocompletesState.onTakeDetailsSelected(event.takeSuggestionDetails)
    }
}