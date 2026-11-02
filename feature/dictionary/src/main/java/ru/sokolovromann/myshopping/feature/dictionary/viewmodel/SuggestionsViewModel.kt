package ru.sokolovromann.myshopping.feature.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.usecase.DeleteFabricsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.DeleteSuggestionsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveDictionaryUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveGeneralPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveSuggestionsPreferencesUseCase
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.dictionary.mapper.SuggestionsMapper
import ru.sokolovromann.myshopping.feature.dictionary.model.SuggestionsState

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
    private val observeDictionaryUseCase: ObserveDictionaryUseCase,
    private val observeSuggestionsPreferencesUseCase: ObserveSuggestionsPreferencesUseCase,
    private val observeGeneralPreferencesUseCase: ObserveGeneralPreferencesUseCase,
    private val deleteSuggestionsUseCase: DeleteSuggestionsUseCase,
    private val deleteFabricsUseCase: DeleteFabricsUseCase,
    private val suggestionsMapper: SuggestionsMapper,
    private val navigator: Navigator,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _suggestionsState: MutableStateFlow<SuggestionsState> = MutableStateFlow(SuggestionsState())
    val suggestionsState: StateFlow<SuggestionsState> = _suggestionsState

    init { onInit() }

    fun onNavigationItemSelected(navigationItem: NavigationItem) = when (navigationItem) {
        NavigationItem.Purchases -> navigator.navigateTo(Screen.Purchases)
        NavigationItem.Archive -> navigator.navigateTo(Screen.Archive)
        NavigationItem.Trash -> navigator.navigateTo(Screen.Trash)
        NavigationItem.Dictionary -> {}
        NavigationItem.Settings -> navigator.navigateTo(Screen.Settings)
        NavigationItem.About -> navigator.navigateTo(Screen.About)
    }

    fun onBackClick() {
        val oldState = _suggestionsState.value
        if (oldState.itemsSelected.isEmpty()) {
            navigator.navigateBack()
        } else {
            val newState = oldState.copy(itemsSelected = emptySet())
            _suggestionsState.tryEmit(newState)
        }
    }

    fun onSuggestionItemClick(uid: UID) {
        val oldState = _suggestionsState.value
        if (oldState.itemsSelected.isNotEmpty()) {
            val newItemSelected = oldState.itemsSelected.toMutableSet().apply {
                if (contains(uid)) remove(uid) else add(uid)
            }
            val newState = oldState.copy(itemsSelected = newItemSelected)
            _suggestionsState.tryEmit(newState)
        }
    }

    fun onSuggestionItemLongClick(uid: UID) {
        val oldState = _suggestionsState.value
        if (oldState.itemsSelected.isEmpty()) {
            val itemSelected = setOf(uid)
            val newState = oldState.copy(itemsSelected = itemSelected)
            _suggestionsState.tryEmit(newState)
        } else { onSuggestionItemClick(uid) }
    }

    fun onAddSuggestionClick() {
        navigator.navigateTo(Screen.AddEditSuggestion())
    }

    fun onEditSuggestionClick() {
        val oldState = _suggestionsState.value

        val uid = oldState.itemsSelected.first()
        val newState = oldState.copy(itemsSelected = emptySet())
        _suggestionsState.tryEmit(newState)

        navigator.navigateTo(Screen.AddEditSuggestion(uid.value))
    }

    fun onDeleteSuggestionsClick() = viewModelScope.launch {
        val oldState = _suggestionsState.value

        val uids = oldState.itemsSelected
        val newState = oldState.copy(itemsSelected = emptySet())
        _suggestionsState.tryEmit(newState)

        deleteSuggestionsUseCase.invoke(uids)
        uids.forEach {
            val directory = FabricDirectory(it)
            deleteFabricsUseCase.invoke(directory)
        }
    }

    fun onSelectAllSuggestionsClick() {
        val oldState = _suggestionsState.value
        val newState = oldState.copy(
            itemsSelected = suggestionsMapper.toUids(oldState.items)
        )
        _suggestionsState.tryEmit(newState)
    }

    private fun onInit() = viewModelScope.launch {
        combine(
            flow = observeDictionaryUseCase(),
            flow2 = observeSuggestionsPreferencesUseCase(),
            flow3 = observeGeneralPreferencesUseCase(),
            transform = { dictionary, suggestionsPreferences, generalPreferences ->
                withContext(mainDispatcher) {
                    suggestionsMapper.createSuggestionState(
                        dictionary,
                        suggestionsPreferences,
                        generalPreferences
                    )
                }
            }
        ).collect { state ->
            withContext(mainDispatcher) {
                _suggestionsState.tryEmit(state)
            }
        }
    }
}