package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.model.AddEditAutocompleteState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent
import javax.inject.Inject

@HiltViewModel
class AddEditAutocompleteViewModel @Inject constructor(
    private val autocompletesRepository: AutocompletesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditAutocompleteEvent> {

    var addEditAutocompleteState: AddEditAutocompleteState by mutableStateOf(AddEditAutocompleteState())
        private set

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
        autocompleteWithConfig: AutocompleteWithConfig
    ) = withContext(AppDispatchers.Main) {
        addEditAutocompleteState.populate(autocompleteWithConfig)
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowKeyboard)
    }

    private fun saveAutocomplete() = viewModelScope.launch {
        addEditAutocompleteState.onWaiting()

        autocompletesRepository.saveAutocomplete(addEditAutocompleteState.getAutocomplete())
            .onSuccess {
                withContext(AppDispatchers.Main) {
                    _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowBackScreen)
                }
            }
            .onFailure {
                if (it is InvalidNameException) {
                    withContext(AppDispatchers.Main) {
                        addEditAutocompleteState.onInvalidNameValue()
                    }
                }
            }
    }

    private fun cancelSavingAutocomplete() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowBackScreen)
    }

    private fun nameChanged(event: AddEditAutocompleteEvent.NameChanged) {
        addEditAutocompleteState.onNameValueChanged(event.value)
    }
}