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
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AutocompletesViewModel @Inject constructor(
    private val autocompletesRepository: AutocompletesRepository,
    private val dispatchers: AppDispatchers
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
        withContext(dispatchers.main) {
            autocompletesState.showLoading()
        }

        autocompletesRepository.getDefaultAutocompletes(getCurrentLanguage()).collect {
            autocompletesLoaded(it, AutocompleteLocation.DEFAULT)
        }
    }

    private fun getPersonalAutocompletes() = viewModelScope.launch {
        withContext(dispatchers.main) {
            autocompletesState.showLoading()
        }

        autocompletesRepository.getPersonalAutocompletes().collect {
            autocompletesLoaded(it, AutocompleteLocation.PERSONAL)
        }
    }

    private suspend fun autocompletesLoaded(
        autocompletes: Autocompletes,
        location: AutocompleteLocation
    ) = withContext(dispatchers.main) {
        if (autocompletes.autocompletes.isEmpty()) {
            autocompletesState.showNotFound(autocompletes.appConfig, location)
        } else {
            autocompletesState.showAutocompletes(autocompletes, location)
        }
    }

    private fun addAutocomplete() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.AddAutocomplete)
    }

    private fun clearAutocompletes() = viewModelScope.launch {
        when (autocompletesState.screenData.location) {
            AutocompleteLocation.DEFAULT -> autocompletesRepository.getDefaultAutocompletes(getCurrentLanguage())
            AutocompleteLocation.PERSONAL -> autocompletesRepository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletesState.screenData.selectedNames?.let { names ->
                val uids = autocompletes.autocompletes
                    .filter { names.contains(it.name.lowercase()) }
                    .map { it.uid }
                autocompletesRepository.clearAutocompletes(
                    uids = uids,
                    lastModified = System.currentTimeMillis()
                )
            }
        }

        withContext(dispatchers.main) {
            unselectAutocompletes()
        }
    }

    private fun deleteAutocompletes() = viewModelScope.launch {
        when (autocompletesState.screenData.location) {
            AutocompleteLocation.DEFAULT -> return@launch
            AutocompleteLocation.PERSONAL -> autocompletesRepository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletesState.screenData.selectedNames?.let { names ->
                val uids = autocompletes.autocompletes
                    .filter { names.contains(it.name.lowercase()) }
                    .map { it.uid }
                autocompletesRepository.deleteAutocompletes(uids)
            }
        }

        withContext(dispatchers.main) {
            unselectAutocompletes()
        }
    }

    private fun selectNavigationItem(
        event: AutocompletesEvent.SelectNavigationItem
    ) = viewModelScope.launch(dispatchers.main) {
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

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.ShowBackScreen)
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.ShowNavigationDrawer)
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.HideNavigationDrawer)
    }

    private fun hideAutocompleteLocation() {
        autocompletesState.hideLocation()
    }

    private fun getCurrentLanguage(): String {
        return Locale.getDefault().language
    }
}