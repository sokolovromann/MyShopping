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
import javax.inject.Inject

@HiltViewModel
class AutocompletesViewModel @Inject constructor(
    private val repository: AutocompletesRepository,
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

            is AutocompletesEvent.ClearAutocomplete -> clearAutocomplete(event)

            is AutocompletesEvent.DeleteAutocomplete -> deleteAutocomplete(event)

            is AutocompletesEvent.SelectNavigationItem -> selectNavigationItem(event)

            AutocompletesEvent.SelectAutocompleteLocation -> selectAutocompleteLocation()

            is AutocompletesEvent.ShowAutocompletes -> showAutocompletes(event)

            AutocompletesEvent.ShowBackScreen -> showBackScreen()

            AutocompletesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is AutocompletesEvent.ShowAutocompleteMenu -> showAutocompleteMenu(event)

            AutocompletesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            AutocompletesEvent.HideAutocompleteLocation -> hideAutocompleteLocation()

            AutocompletesEvent.HideAutocompleteMenu -> hideAutocompleteMenu()
        }
    }

    private fun getDefaultAutocompletes() = viewModelScope.launch {
        withContext(dispatchers.main) {
            autocompletesState.showLoading()
        }

        repository.getDefaultAutocompletes().collect {
            autocompletesLoaded(it, AutocompleteLocation.DEFAULT)
        }
    }

    private fun getPersonalAutocompletes() = viewModelScope.launch {
        withContext(dispatchers.main) {
            autocompletesState.showLoading()
        }

        repository.getPersonalAutocompletes().collect {
            autocompletesLoaded(it, AutocompleteLocation.PERSONAL)
        }
    }

    private suspend fun autocompletesLoaded(
        autocompletes: Autocompletes,
        location: AutocompleteLocation
    ) = withContext(dispatchers.main) {
        if (autocompletes.autocompletes.isEmpty()) {
            autocompletesState.showNotFound(autocompletes.preferences, location)
        } else {
            autocompletesState.showAutocompletes(autocompletes, location)
        }
    }

    private fun addAutocomplete() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.AddAutocomplete)
    }

    private fun clearAutocomplete(
        event: AutocompletesEvent.ClearAutocomplete
    ) = viewModelScope.launch {
        when (autocompletesState.screenData.location) {
            AutocompleteLocation.DEFAULT -> repository.getDefaultAutocompletes()
            AutocompleteLocation.PERSONAL -> repository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletes.autocompletes
                .filter { it.name.lowercase() == event.name.lowercase() }
                .forEach {
                    val autocomplete = it.copy(
                        lastModified = System.currentTimeMillis(),
                        quantity = Quantity(),
                        price = Money(),
                        discount = Discount(),
                        taxRate = TaxRate(),
                        total = Money()
                    )
                    repository.clearAutocomplete(autocomplete)
                }
        }

        withContext(dispatchers.main) {
            hideAutocompleteMenu()
        }
    }

    private fun deleteAutocomplete(
        event: AutocompletesEvent.DeleteAutocomplete
    ) = viewModelScope.launch {
        when (autocompletesState.screenData.location) {
            AutocompleteLocation.DEFAULT -> return@launch
            AutocompleteLocation.PERSONAL -> repository.getPersonalAutocompletes()
        }.firstOrNull()?.let { autocompletes ->
            autocompletes.autocompletes
                .filter { it.name.lowercase() == event.name.lowercase() }
                .forEach { repository.deleteAutocomplete(it.uid) }
        }

        withContext(dispatchers.main) {
            hideAutocompleteMenu()
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

    private fun showAutocompleteMenu(event: AutocompletesEvent.ShowAutocompleteMenu) {
        autocompletesState.showAutocompleteMenu(event.uid)
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.HideNavigationDrawer)
    }

    private fun hideAutocompleteLocation() {
        autocompletesState.hideLocation()
    }

    private fun hideAutocompleteMenu() {
        autocompletesState.hideAutocompleteMenu()
    }
}