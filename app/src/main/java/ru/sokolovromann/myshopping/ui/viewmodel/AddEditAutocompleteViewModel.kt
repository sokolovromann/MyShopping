package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.AddEditAutocompleteState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent
import javax.inject.Inject

@HiltViewModel
class AddEditAutocompleteViewModel @Inject constructor(
    private val autocompletesRepository: AutocompletesRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditAutocompleteEvent> {

    val addEditAutocompleteState: AddEditAutocompleteState = AddEditAutocompleteState()

    private val _screenEventFlow: MutableSharedFlow<AddEditAutocompleteScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AddEditAutocompleteScreenEvent> = _screenEventFlow

    private val uid: String? = savedStateHandle.get<String>(UiRouteKey.AutocompleteUid.key)

    init {
        getAutocomplete()
    }

    override fun onEvent(event: AddEditAutocompleteEvent) {
        when (event) {
            AddEditAutocompleteEvent.SaveAutocomplete -> saveAutocomplete()

            AddEditAutocompleteEvent.CancelSavingAutocomplete -> cancelSavingAutocomplete()

            is AddEditAutocompleteEvent.NameChanged -> nameChanged(event)
        }
    }

    private fun getAutocomplete() = viewModelScope.launch {
        autocompletesRepository.getAutocomplete(uid).firstOrNull()?.let {
            autocompleteLoaded(it)
        }
    }

    private suspend fun autocompleteLoaded(
        autocomplete: AutocompleteWithConfig
    ) = withContext(dispatchers.main) {
        addEditAutocompleteState.populate(autocomplete)
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowKeyboard)
    }

    private fun saveAutocomplete() = viewModelScope.launch {
        val autocomplete = addEditAutocompleteState.getCurrentAutocomplete()
        autocompletesRepository.saveAutocomplete(autocomplete)

        withContext(dispatchers.main) {
            _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowBackScreen)
        }
    }

    private fun cancelSavingAutocomplete() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowBackScreen)
    }

    private fun nameChanged(event: AddEditAutocompleteEvent.NameChanged) {
        addEditAutocompleteState.changeNameValue(event.value)
    }
}