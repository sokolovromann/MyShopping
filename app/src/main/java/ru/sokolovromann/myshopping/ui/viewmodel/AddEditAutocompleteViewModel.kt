package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.model.AddEditAutocompleteState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAutocompleteViewModel @Inject constructor(
    private val autocompletesRepository: AutocompletesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditAutocompleteEvent> {

    val addEditAutocompleteState: AddEditAutocompleteState = AddEditAutocompleteState()

    private val _screenEventFlow: MutableSharedFlow<AddEditAutocompleteScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AddEditAutocompleteScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: AddEditAutocompleteEvent) {
        when (event) {
            AddEditAutocompleteEvent.OnClickSave -> onClickSave()

            AddEditAutocompleteEvent.OnClickCancel -> onClickCancel()

            is AddEditAutocompleteEvent.OnNameValueChanged -> onNameValueChanged(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.AutocompleteUid.key)

        autocompletesRepository.getAutocomplete(uid).firstOrNull()?.let {
            addEditAutocompleteState.populate(it)
            _screenEventFlow.emit(AddEditAutocompleteScreenEvent.OnShowKeyboard)
        }
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        addEditAutocompleteState.onWaiting()

        autocompletesRepository.saveAutocomplete(addEditAutocompleteState.getAutocomplete())
            .onSuccess { _screenEventFlow.emit(AddEditAutocompleteScreenEvent.OnShowBackScreen) }
            .onFailure { addEditAutocompleteState.onInvalidNameValue() }
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.OnShowBackScreen)
    }

    private fun onNameValueChanged(event: AddEditAutocompleteEvent.OnNameValueChanged) {
        addEditAutocompleteState.onNameValueChanged(event.value)
    }
}