package ru.sokolovromann.myshopping.feature.dictionary.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.usecase.DeleteFabricsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.GetSupportUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.InsertSuggestionsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveGeneralPreferencesUseCase
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.feature.dictionary.mapper.AddEditSuggestionMapper
import ru.sokolovromann.myshopping.feature.dictionary.model.AddEditSuggestionState

@HiltViewModel
class AddEditSuggestionViewModel @Inject constructor(
    private val observeGeneralPreferencesUseCase: ObserveGeneralPreferencesUseCase,
    private val getSupportUseCase: GetSupportUseCase,
    private val insertSuggestionsUseCase: InsertSuggestionsUseCase,
    private val deleteFabricsUseCase: DeleteFabricsUseCase,
    private val addEditSuggestionMapper: AddEditSuggestionMapper,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _addEditSuggestionState: MutableStateFlow<AddEditSuggestionState> =
        MutableStateFlow(addEditSuggestionMapper.getDefaultAddEditSuggestionState())
    val addEditSuggestionState: StateFlow<AddEditSuggestionState> = _addEditSuggestionState

    private val suggestionUid: UID? get() = savedStateHandle
        .toRoute<Screen.AddEditSuggestion>().uid
        ?.let { UID(it) }

    private val deletedFabricUids: MutableSet<UID> = mutableSetOf()

    init { onInit() }

    fun onBackClick() {
        navigator.navigateBack()
    }

    fun onSaveSuggestionClick() = viewModelScope.launch(mainDispatcher) {
        val name = _addEditSuggestionState.value.name.text.trim()
        if (name.isEmpty()) {
            val newState = _addEditSuggestionState.value.copy(isNameError = true)
            _addEditSuggestionState.tryEmit(newState)
            return@launch
        }

        if (suggestionUid == null) {
            val newSuggestion = addEditSuggestionMapper.createSuggestion(name)
            insertSuggestionsUseCase(newSuggestion)
        } else {
            getSupportUseCase(suggestionUid!!)?.let {
                val newSuggestion = addEditSuggestionMapper.changeSuggestion(it, name)
                insertSuggestionsUseCase(newSuggestion)
            }
            deleteFabricsUseCase(deletedFabricUids)
        }

        navigator.navigateBack()
    }

    fun onDeleteFabricClick(uid: UID) {
        deletedFabricUids.add(uid)

        val oldState = _addEditSuggestionState.value
        val newFabrics = oldState.fabrics.mapValues { (_, values) ->
            values.filter { it.uid != uid }
        }
        val newState = oldState.copy(fabrics = newFabrics)
        _addEditSuggestionState.tryEmit(newState)
    }

    fun onSuggestionNameChange(value: TextFieldValue) {
        val newState = _addEditSuggestionState.value.copy(
            name = value,
            isNameError = false
        )
        _addEditSuggestionState.tryEmit(newState)
    }

    private fun onInit() = viewModelScope.launch {
        if (suggestionUid == null) {
            withContext(mainDispatcher) {
                val newState = addEditSuggestionMapper.getDefaultAddEditSuggestionState()
                _addEditSuggestionState.tryEmit(newState)
            }
        } else {
            val support = getSupportUseCase(suggestionUid!!)
            val generalPreferences = observeGeneralPreferencesUseCase().first()
            withContext(mainDispatcher) {
                deletedFabricUids.clear()

                val newState = if (support == null) {
                    addEditSuggestionMapper.getDefaultAddEditSuggestionState()
                } else {
                    addEditSuggestionMapper.createAddEditSuggestionState(support, generalPreferences)
                }
                _addEditSuggestionState.tryEmit(newState)
            }
        }
    }
}