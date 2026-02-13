package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDirectory
import ru.sokolovromann.myshopping.manager.SuggestionsManager
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.model.AddEditAutocompleteState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import ru.sokolovromann.myshopping.utils.SavedStateHandleExtensions.getUid
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime
import javax.inject.Inject

@HiltViewModel
class AddEditAutocompleteViewModel @Inject constructor(
    private val suggestionsManager: SuggestionsManager,
    private val appConfigRepository: AppConfigRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditAutocompleteEvent> {

    private val suggestionUid: UID = savedStateHandle.getUid(UiRouteKey.AutocompleteUid.key)

    val addEditAutocompleteState: AddEditAutocompleteState = AddEditAutocompleteState()

    private val _screenEventFlow: MutableSharedFlow<AddEditAutocompleteScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AddEditAutocompleteScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    private val deletedDetailsUids: MutableCollection<UID> = mutableListOf()

    init { onInit() }

    override fun onEvent(event: AddEditAutocompleteEvent) {
        when (event) {
            AddEditAutocompleteEvent.OnClickSave -> onClickSave()

            AddEditAutocompleteEvent.OnClickCancel -> onClickCancel()

            is AddEditAutocompleteEvent.OnNameValueChanged -> onNameValueChanged(event)

            is AddEditAutocompleteEvent.OnClickDeleteDetail -> onClickDeleteDetail(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        val userPreferences = appConfigRepository.getAppConfig().first().userPreferences
        if (suggestionUid.value.isEmpty()) {
            _screenEventFlow.emit(AddEditAutocompleteScreenEvent.OnShowKeyboard)
        } else {
            suggestionsManager.getSuggestionWithDetails(suggestionUid)
                ?.let { addEditAutocompleteState.populate(it, userPreferences) }
        }
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        addEditAutocompleteState.onWaiting()

        val newName = addEditAutocompleteState.nameValue.text
        if (newName.isEmpty()) {
            addEditAutocompleteState.onInvalidNameValue()
        } else {
            val oldSuggestionWithDetails = suggestionsManager.getSuggestionWithDetails(suggestionUid)
            val currentDateTime = DateTime.getCurrent()
            val newSuggestion = if (oldSuggestionWithDetails?.suggestion == null) {
                Suggestion(
                    uid = UID.createRandom(),
                    directory = SuggestionDirectory.NoDirectory,
                    created = currentDateTime,
                    lastModified = currentDateTime,
                    name = newName,
                    used = 0
                )
            } else {
                oldSuggestionWithDetails.suggestion.copy(
                    lastModified = currentDateTime,
                    name = newName,
                )
            }
            suggestionsManager.apply {
                addSuggestion(newSuggestion)
                deleteDetails(suggestionUid, deletedDetailsUids)
            }
            deletedDetailsUids.clear()
            _screenEventFlow.emit(AddEditAutocompleteScreenEvent.OnShowBackScreen)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.OnShowBackScreen)
    }

    private fun onNameValueChanged(event: AddEditAutocompleteEvent.OnNameValueChanged) {
        addEditAutocompleteState.onNameValueChanged(event.value)
    }

    private fun onClickDeleteDetail(event: AddEditAutocompleteEvent.OnClickDeleteDetail) {
        deletedDetailsUids.add(event.uid)
    }
}