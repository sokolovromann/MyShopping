package ru.sokolovromann.myshopping.feature.dictionary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.dictionary.domain.usecase.ObserveDictionaryUseCase
import ru.sokolovromann.myshopping.feature.dictionary.ui.mapper.SuggestionsStateMapper
import ru.sokolovromann.myshopping.feature.dictionary.ui.model.SuggestionsState

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    private val fabricsRepository: FabricsRepository,
    private val observeDictionaryUseCase: ObserveDictionaryUseCase,
    private val stateMapper: SuggestionsStateMapper,
    private val navigator: Navigator,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _suggestionsState: MutableStateFlow<SuggestionsState> =
        MutableStateFlow(SuggestionsState())
    val suggestionsState: StateFlow<SuggestionsState> = _suggestionsState

    init { observeDictionary() }

    fun onNavigationItemSelected(navigationItem: NavigationItem) = when (navigationItem) {
        NavigationItem.Purchases -> navigator.navigateTo(Screen.Purchases)
        NavigationItem.Archive -> navigator.navigateTo(Screen.Archive)
        NavigationItem.Trash -> navigator.navigateTo(Screen.Trash)
        NavigationItem.Suggestions -> {}
        NavigationItem.Settings -> navigator.navigateTo(Screen.Settings)
        NavigationItem.About -> navigator.navigateTo(Screen.About)
    }

    fun onBackClick() {
        if (_suggestionsState.value.selected.isEmpty()) {
            navigator.navigateBack()
        } else {
            val newState = _suggestionsState.value.copy(
                selected = emptySet()
            )
            _suggestionsState.tryEmit(newState)
        }
    }

    fun onSuggestionItemClick(uid: UID) {
        val oldState = _suggestionsState.value
        if (oldState.selected.isNotEmpty()) {
            val newItemSelected = oldState.selected.toMutableSet().apply {
                if (contains(uid)) remove(uid) else add(uid)
            }
            val newState = oldState.copy(selected = newItemSelected)
            _suggestionsState.tryEmit(newState)
        }
    }

    fun onSuggestionItemLongClick(uid: UID) {
        val oldState = _suggestionsState.value
        if (oldState.selected.isEmpty()) {
            val itemSelected = setOf(uid)
            val newState = oldState.copy(selected = itemSelected)
            _suggestionsState.tryEmit(newState)
        } else { onSuggestionItemClick(uid) }
    }

    fun onAddSuggestionClick() {
        navigator.navigateTo(Screen.AddEditSuggestion())
    }

    fun onEditSuggestionClick() {
        val uid = _suggestionsState.value.selected.first()

        val newState = _suggestionsState.value.copy(selected = emptySet())
        _suggestionsState.tryEmit(newState)

        navigator.navigateTo(Screen.AddEditSuggestion(uid.value))
    }

    fun onDeleteSuggestionsClick() = viewModelScope.launch {
        val uids = _suggestionsState.value.selected

        val newState = _suggestionsState.value.copy(selected = emptySet())
        _suggestionsState.tryEmit(newState)

        suggestionsRepository.deleteSuggestions(uids)
        uids.forEach {
            val directory = FabricDirectory(it)
            fabricsRepository.deleteFabrics(directory)
        }
    }

    fun onSelectAllSuggestionsClick() {
        val oldState = _suggestionsState.value
        val newState = oldState.copy(
            selected = stateMapper.toUids(oldState.suggestions)
        )
        _suggestionsState.tryEmit(newState)
    }

    private fun observeDictionary() = viewModelScope.launch {
        observeDictionaryUseCase()
            .onStart {
                val state = SuggestionsState(isProgress = true)
                _suggestionsState.tryEmit(state)
            }
            .collect { dictionary ->
                withContext(mainDispatcher) {
                    val state = stateMapper.toSuggestionsState(dictionary)
                    _suggestionsState.tryEmit(state)
                }
            }
    }
}