package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfig
import ru.sokolovromann.myshopping.manager.SuggestionsManager
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.AutocompletesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class AutocompletesViewModel @Inject constructor(
    private val suggestionsManager: SuggestionsManager,
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<AutocompletesEvent> {

    val autocompletesState: AutocompletesState = AutocompletesState()

    private val _screenEventFlow: MutableSharedFlow<AutocompletesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AutocompletesScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: AutocompletesEvent) {
        when (event) {
            AutocompletesEvent.OnClickAddAutocomplete -> onClickAddAutocomplete()

            AutocompletesEvent.OnClickClearAutocompletes -> onClickClearAutocompletes()

            AutocompletesEvent.OnClickDeleteAutocompletes -> onClickDeleteAutocompletes()

            AutocompletesEvent.OnClickBack -> onClickBack()

            is AutocompletesEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is AutocompletesEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)

            is AutocompletesEvent.OnAllAutocompletesSelected -> onAllAutocompletesSelected(event)

            is AutocompletesEvent.OnAutocompleteSelected -> onAutocompleteSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        autocompletesState.onWaiting()
        combine(
            flow = suggestionsManager.observeSuggestionsWithDetails(),
            flow2 = suggestionsManager.observeConfig(),
            flow3 = appConfigRepository.getAppConfig(),
            transform = { suggestions, suggestionsConfig, appConfig ->
                AutocompletesData(suggestions, suggestionsConfig, appConfig.userPreferences.currency)
            }
        ).collect { autocompletesState.populate(it) }
    }

    private fun onClickAddAutocomplete() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnShowAddAutocompleteScreen)
    }

    private fun onClickClearAutocompletes() = viewModelScope.launch(dispatcher) {
        autocompletesState.selectedUids?.forEach {
            suggestionsManager.deleteDetails(it)
        }
        autocompletesState.onAllAutocompletesSelected(selected = false)
    }

    private fun onClickDeleteAutocompletes() = viewModelScope.launch(dispatcher) {
        autocompletesState.selectedUids?.let {
            suggestionsManager.deleteSuggestionsWithDetails(it)
        }
        autocompletesState.onAllAutocompletesSelected(selected = false)
    }

    private fun onClickBack() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onDrawerScreenSelected(
        event: AutocompletesEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: AutocompletesEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onAllAutocompletesSelected(event: AutocompletesEvent.OnAllAutocompletesSelected) {
        autocompletesState.onAllAutocompletesSelected(event.selected)
    }

    private fun onAutocompleteSelected(event: AutocompletesEvent.OnAutocompleteSelected) {
        autocompletesState.onAutocompleteSelected(event.selected, event.uid)
    }

    data class AutocompletesData(
        val suggestionsWithDetails: Collection<SuggestionWithDetails>,
        val suggestionsConfig: SuggestionsConfig,
        val currency: Currency
    )
}