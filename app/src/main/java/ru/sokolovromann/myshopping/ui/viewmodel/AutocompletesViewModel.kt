package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.AutocompleteLocation
import ru.sokolovromann.myshopping.ui.model.AutocompletesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent
import javax.inject.Inject

@HiltViewModel
class AutocompletesViewModel @Inject constructor(
    private val autocompletesRepository: AutocompletesRepository
) : ViewModel(), ViewModelEvent<AutocompletesEvent> {

    val autocompletesState: AutocompletesState = AutocompletesState()

    private val _screenEventFlow: MutableSharedFlow<AutocompletesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AutocompletesScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: AutocompletesEvent) {
        when (event) {
            AutocompletesEvent.OnClickAddAutocomplete -> onClickAddAutocomplete()

            AutocompletesEvent.OnClickClearAutocompletes -> onClickClearAutocompletes()

            AutocompletesEvent.OnClickDeleteAutocompletes -> onClickDeleteAutocompletes()

            AutocompletesEvent.OnClickBack -> onClickBack()

            is AutocompletesEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is AutocompletesEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)

            is AutocompletesEvent.OnLocationSelected -> onLocationSelected(event)

            is AutocompletesEvent.OnSelectLocation -> onSelectLocation(event)

            is AutocompletesEvent.OnAllAutocompletesSelected -> onAllAutocompletesSelected(event)

            is AutocompletesEvent.OnAutocompleteSelected -> onAutocompleteSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        autocompletesState.onWaiting()

        autocompletesRepository.getPersonalAutocompletes().collect {
            autocompletesState.populate(it, AutocompleteLocation.PERSONAL)
        }
    }

    private fun onClickAddAutocomplete() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnShowAddAutocompleteScreen)
    }

    private fun onClickClearAutocompletes() = viewModelScope.launch(AppDispatchers.Main) {
        when (autocompletesState.locationValue.selected) {
            AutocompleteLocation.DEFAULT -> autocompletesRepository.getDefaultAutocompletes()
            AutocompleteLocation.PERSONAL -> autocompletesRepository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletesState.selectedNames?.let { names ->
                val uids = autocompletes.getUidsByNames(names)
                autocompletesRepository.clearAutocompletes(uids = uids)
            }
        }

        autocompletesState.onAllAutocompletesSelected(selected = false)
    }

    private fun onClickDeleteAutocompletes() = viewModelScope.launch(AppDispatchers.Main) {
        when (autocompletesState.locationValue.selected) {
            AutocompleteLocation.DEFAULT -> return@launch
            AutocompleteLocation.PERSONAL -> autocompletesRepository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletesState.selectedNames?.let { names ->
                val uids = autocompletes.getUidsByNames(names)
                autocompletesRepository.deleteAutocompletes(uids)
            }
        }

        autocompletesState.onAllAutocompletesSelected(selected = false)
    }

    private fun onClickBack() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onDrawerScreenSelected(
        event: AutocompletesEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: AutocompletesEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onLocationSelected(
        event: AutocompletesEvent.OnLocationSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        autocompletesState.onSelectLocation(false)
        autocompletesState.onWaiting()

        when (event.location) {
            AutocompleteLocation.DEFAULT -> autocompletesRepository.getDefaultAutocompletes()
            AutocompleteLocation.PERSONAL -> autocompletesRepository.getPersonalAutocompletes()
        }.collect {
            autocompletesState.populate(it, event.location)
        }
    }

    private fun onSelectLocation(event: AutocompletesEvent.OnSelectLocation) {
        autocompletesState.onSelectLocation(event.expanded)
    }

    private fun onAllAutocompletesSelected(event: AutocompletesEvent.OnAllAutocompletesSelected) {
        autocompletesState.onAllAutocompletesSelected(event.selected)
    }

    private fun onAutocompleteSelected(event: AutocompletesEvent.OnAutocompleteSelected) {
        autocompletesState.onAutocompleteSelected(event.selected, event.name)
    }
}