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
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent
import javax.inject.Inject

@HiltViewModel
class AutocompletesViewModel @Inject constructor(
    private val autocompletesRepository: AutocompletesRepository
) : ViewModel(), ViewModelEvent<AutocompletesEvent> {

    val autocompletesState: AutocompletesState = AutocompletesState()

    private val _screenEventFlow: MutableSharedFlow<AutocompletesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AutocompletesScreenEvent> = _screenEventFlow

    init {
        getPersonalAutocompletes()
    }

    override fun onEvent(event: AutocompletesEvent) {
        when (event) {
            AutocompletesEvent.AddAutocomplete -> addAutocomplete()

            AutocompletesEvent.ClearAutocompletes -> clearAutocompletes()

            AutocompletesEvent.DeleteAutocompletes -> deleteAutocompletes()

            is AutocompletesEvent.SelectNavigationItem -> selectNavigationItem(event)

            AutocompletesEvent.SelectAutocompleteLocation -> selectAutocompleteLocation()

            AutocompletesEvent.SelectAllAutocompletes -> selectAllAutocompletes()

            is AutocompletesEvent.SelectAutocomplete -> selectAutocomplete(event)

            is AutocompletesEvent.UnselectAutocomplete -> unselectAutocomplete(event)

            AutocompletesEvent.CancelSelectingAutocompletes -> cancelSelectingAutocompletes()

            is AutocompletesEvent.ShowAutocompletes -> showAutocompletes(event)

            AutocompletesEvent.ShowBackScreen -> showBackScreen()

            AutocompletesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            AutocompletesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            AutocompletesEvent.HideAutocompleteLocation -> hideAutocompleteLocation()
        }
    }

    private fun getDefaultAutocompletes() = viewModelScope.launch {
        withContext(AppDispatchers.Main) {
            autocompletesState.showLoading()
        }

        autocompletesRepository.getDefaultAutocompletes().collect {
            autocompletesLoaded(it, AutocompleteLocation.DEFAULT)
        }
    }

    private fun getPersonalAutocompletes() = viewModelScope.launch {
        withContext(AppDispatchers.Main) {
            autocompletesState.showLoading()
        }

        autocompletesRepository.getPersonalAutocompletes().collect {
            autocompletesLoaded(it, AutocompleteLocation.PERSONAL)
        }
    }

    private suspend fun autocompletesLoaded(
        autocompletes: AutocompletesWithConfig,
        location: AutocompleteLocation
    ) = withContext(AppDispatchers.Main) {
        if (autocompletes.isEmpty()) {
            autocompletesState.showNotFound(autocompletes, location)
        } else {
            autocompletesState.showAutocompletes(autocompletes, location)
        }
    }

    private fun addAutocomplete() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.AddAutocomplete)
    }

    private fun clearAutocompletes() = viewModelScope.launch {
        when (autocompletesState.screenData.location) {
            AutocompleteLocation.DEFAULT -> autocompletesRepository.getDefaultAutocompletes()
            AutocompleteLocation.PERSONAL -> autocompletesRepository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletesState.screenData.selectedNames?.let { names ->
                val uids = autocompletes.getUidsByNames(names)
                autocompletesRepository.clearAutocompletes(uids = uids)
            }
        }

        withContext(AppDispatchers.Main) {
            unselectAutocompletes()
        }
    }

    private fun deleteAutocompletes() = viewModelScope.launch {
        when (autocompletesState.screenData.location) {
            AutocompleteLocation.DEFAULT -> return@launch
            AutocompleteLocation.PERSONAL -> autocompletesRepository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletesState.screenData.selectedNames?.let { names ->
                val uids = autocompletes.getUidsByNames(names)
                autocompletesRepository.deleteAutocompletes(uids)
            }
        }

        withContext(AppDispatchers.Main) {
            unselectAutocompletes()
        }
    }

    private fun selectNavigationItem(
        event: AutocompletesEvent.SelectNavigationItem
    ) = viewModelScope.launch(AppDispatchers.Main) {
        when (event.route) {
            UiRoute.Purchases -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowPurchases)
            UiRoute.Archive -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowArchive)
            UiRoute.Trash -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowTrash)
            UiRoute.Settings -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowSettings)
            else -> return@launch
        }
    }

    private fun selectAutocompleteLocation() {
        autocompletesState.showLocation()
    }

    private fun selectAllAutocompletes() {
        autocompletesState.selectAllAutocompletes()
    }

    private fun selectAutocomplete(event: AutocompletesEvent.SelectAutocomplete) {
        autocompletesState.selectAutocomplete(event.name)
    }

    private fun unselectAutocomplete(event: AutocompletesEvent.UnselectAutocomplete) {
        autocompletesState.unselectAutocomplete(event.name)
    }

    private fun unselectAutocompletes() {
        autocompletesState.unselectAllAutocompletes()
    }

    private fun cancelSelectingAutocompletes() {
        unselectAutocompletes()
    }

    private fun showAutocompletes(event: AutocompletesEvent.ShowAutocompletes) {
        hideAutocompleteLocation()

        when (event.location) {
            AutocompleteLocation.DEFAULT -> getDefaultAutocompletes()
            AutocompleteLocation.PERSONAL -> getPersonalAutocompletes()
        }
    }

    private fun showBackScreen() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.ShowBackScreen)
    }

    private fun showNavigationDrawer() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.ShowNavigationDrawer)
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.HideNavigationDrawer)
    }

    private fun hideAutocompleteLocation() {
        autocompletesState.hideLocation()
    }
}