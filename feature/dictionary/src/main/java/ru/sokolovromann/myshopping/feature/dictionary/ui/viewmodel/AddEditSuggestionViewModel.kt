package ru.sokolovromann.myshopping.feature.dictionary.ui.viewmodel

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.feature.dictionary.domain.usecase.GetSupportUseCase
import ru.sokolovromann.myshopping.feature.dictionary.ui.mapper.AddEditSuggestionStateMapper
import ru.sokolovromann.myshopping.feature.dictionary.ui.model.AddEditSuggestionState

@HiltViewModel
class AddEditSuggestionViewModel @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    private val fabricsRepository: FabricsRepository,
    private val getSupportUseCase: GetSupportUseCase,
    private val stateMapper: AddEditSuggestionStateMapper,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _addEditSuggestionState: MutableStateFlow<AddEditSuggestionState> =
        MutableStateFlow(AddEditSuggestionState())
    val addEditSuggestionState: StateFlow<AddEditSuggestionState> = _addEditSuggestionState

    private val suggestionUid: UID? get() = savedStateHandle
        .toRoute<Screen.AddEditSuggestion>().uid
        ?.let { UID(it) }

    private val deletedFabricUids: MutableSet<UID> = mutableSetOf()

    init { getSupport() }

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
            val newSuggestion = stateMapper.toSuggestion(name)
            suggestionsRepository.insertSuggestions(listOf(newSuggestion))
        } else {
            getSupportUseCase(suggestionUid!!).suggestionWithFabrics?.let {
                val newSuggestion = stateMapper.copySuggestion(it.suggestion, name)
                suggestionsRepository.insertSuggestions(listOf(newSuggestion))
            }
            fabricsRepository.deleteFabrics(deletedFabricUids)
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

    private fun getSupport() = viewModelScope.launch {
        if (suggestionUid == null) {
            withContext(mainDispatcher) {
                _addEditSuggestionState.tryEmit(AddEditSuggestionState())
            }
        } else {
            val support = getSupportUseCase(suggestionUid!!)
            withContext(mainDispatcher) {
                deletedFabricUids.clear()

                val state = if (support.suggestionWithFabrics == null) {
                    AddEditSuggestionState()
                } else {
                    stateMapper.toAddEditSuggestionState(support)
                }
                _addEditSuggestionState.tryEmit(state)
            }
        }
    }
}