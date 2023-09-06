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
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
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
        getAddEditAutocomplete()
    }

    override fun onEvent(event: AddEditAutocompleteEvent) {
        when (event) {
            AddEditAutocompleteEvent.SaveAutocomplete -> saveAutocomplete()

            AddEditAutocompleteEvent.CancelSavingAutocomplete -> cancelSavingAutocomplete()

            is AddEditAutocompleteEvent.NameChanged -> nameChanged(event)
        }
    }

    private fun getAddEditAutocomplete() = viewModelScope.launch {
        autocompletesRepository.getAddEditAutocomplete(uid).firstOrNull()?.let {
            addEditAutocompleteLoaded(it)
        }
    }

    private suspend fun addEditAutocompleteLoaded(
        addEditAutocomplete: AddEditAutocomplete
    ) = withContext(dispatchers.main) {
        addEditAutocompleteState.populate(addEditAutocomplete)
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowKeyboard)
    }

    private fun saveAutocomplete() = viewModelScope.launch {
        val autocomplete = addEditAutocompleteState.getAutocompleteResult()
            .getOrElse { return@launch }

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