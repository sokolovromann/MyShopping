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
import ru.sokolovromann.myshopping.data.repository.AddEditAutocompleteRepository
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.AddEditAutocompleteState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent
import javax.inject.Inject

@HiltViewModel
class AddEditAutocompleteViewModel @Inject constructor(
    private val repository: AddEditAutocompleteRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditAutocompleteEvent> {

    val addEditAutocompleteState: AddEditAutocompleteState = AddEditAutocompleteState()

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

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

    private fun getAutocomplete() = viewModelScope.launch(dispatchers.io) {
        repository.getAddEditAutocomplete(uid).firstOrNull()?.let {
            showAutocomplete(it)
        }
    }

    private fun saveAutocomplete() = viewModelScope.launch(dispatchers.io) {
        addEditAutocompleteState.getAutocompleteResult()
            .onSuccess {
                if (uid == null) {
                    repository.addAutocomplete(it)
                } else {
                    repository.editAutocomplete(it)
                }

                hideKeyboard()
                showBackScreen()
            }
            .onFailure { return@launch }
    }

    private fun cancelSavingAutocomplete() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun nameChanged(event: AddEditAutocompleteEvent.NameChanged) {
        addEditAutocompleteState.changeNameValue(event.value)
    }

    private suspend fun showAutocomplete(
        addEditAutocomplete: AddEditAutocomplete
    ) = withContext(dispatchers.main) {
        addEditAutocompleteState.populate(addEditAutocomplete)
        showKeyboard()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowBackScreen)
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(true)
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }
}