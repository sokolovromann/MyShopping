package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
        getAutocompletes()
    }

    override fun onEvent(event: AutocompletesEvent) {
        when (event) {
            AutocompletesEvent.AddAutocomplete -> addAutocomplete()

            is AutocompletesEvent.EditAutocomplete -> editAutocomplete(event)

            is AutocompletesEvent.DeleteAutocomplete -> deleteAutocomplete(event)

            is AutocompletesEvent.SelectNavigationItem -> selectNavigationItem(event)

            AutocompletesEvent.ShowBackScreen -> showBackScreen()

            AutocompletesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is AutocompletesEvent.ShowAutocompleteMenu -> showAutocompleteMenu(event)

            AutocompletesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            AutocompletesEvent.HideAutocompleteMenu -> hideAutocompleteMenu()
        }
    }

    private fun getAutocompletes() = viewModelScope.launch {
        withContext(dispatchers.main) {
            autocompletesState.showLoading()
        }

        repository.getAutocompletes().collect {
            autocompletesLoaded(it)
        }
    }

    private suspend fun autocompletesLoaded(
        autocompletes: Autocompletes
    ) = withContext(dispatchers.main) {
        if (autocompletes.autocompletes.isEmpty()) {
            autocompletesState.showNotFound(autocompletes.preferences)
        } else {
            autocompletesState.showAutocompletes(autocompletes)
        }
    }

    private fun addAutocomplete() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.AddAutocomplete)
    }

    private fun editAutocomplete(
        event: AutocompletesEvent.EditAutocomplete
    ) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.EditAutocomplete(event.uid))
        hideAutocompleteMenu()
    }

    private fun deleteAutocomplete(
        event: AutocompletesEvent.DeleteAutocomplete
    ) = viewModelScope.launch {
        repository.deleteAutocomplete(event.uid)

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

    private fun hideAutocompleteMenu() {
        autocompletesState.hideAutocompleteMenu()
    }
}