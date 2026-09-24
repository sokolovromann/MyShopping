package ru.sokolovromann.myshopping.feature.addeditsuggestion

import androidx.compose.ui.text.TextRange
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
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.SuggestionDirectory
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.usecase.GetSupportUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.InsertSuggestionsUseCase
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen

@HiltViewModel
class AddEditSuggestionViewModel @Inject constructor(
    private val getSupportUseCase: GetSupportUseCase,
    private val insertSuggestionsUseCase: InsertSuggestionsUseCase,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _addEditSuggestionState: MutableStateFlow<AddEditSuggestionState> =
        MutableStateFlow(AddEditSuggestionState())
    val addEditSuggestionState: StateFlow<AddEditSuggestionState> = _addEditSuggestionState

    init { onInit() }

    fun onBackClick() {
        navigator.navigateBack()
    }

    fun onSaveSuggestionClick() = viewModelScope.launch(mainDispatcher) {
        val name = _addEditSuggestionState.value.name.text
        if (name.isEmpty()) {
            val newState = _addEditSuggestionState.value.copy(isNameError = true)
            _addEditSuggestionState.tryEmit(newState)
            return@launch
        }

        val uid = _addEditSuggestionState.value.uid
        val timeInMillis = TimeInMillis.getCurrent()
        val newSuggestion = if (uid == null) {
            Suggestion(
                uid = UID.createRandom(),
                directory = SuggestionDirectory.NoDirectory,
                created = timeInMillis,
                lastModified = timeInMillis,
                name = name,
                used = 0
            )
        } else {
            getSupportUseCase(uid)?.let {
                Suggestion(
                    uid = it.uid,
                    directory = it.directory,
                    created = it.created,
                    lastModified = timeInMillis,
                    name = name,
                    used = it.used.plus(1)
                )
            }
        }
        if (newSuggestion != null) {
            insertSuggestionsUseCase(newSuggestion)
        }
        navigator.navigateBack()
    }

    fun onSuggestionNameChange(value: TextFieldValue) {
        val newState = _addEditSuggestionState.value.copy(
            name = value,
            isNameError = false
        )
        _addEditSuggestionState.tryEmit(newState)
    }

    private fun onInit() = viewModelScope.launch {
        val uidValue = savedStateHandle.toRoute<Screen.AddEditSuggestion>().uid
        if (uidValue == null) {
            withContext(mainDispatcher) {
                _addEditSuggestionState.tryEmit(AddEditSuggestionState())
            }
        } else {
            val uid = UID(uidValue)
            val support = getSupportUseCase(uid)
            withContext(mainDispatcher) {
                val state = if (support == null) {
                    AddEditSuggestionState()
                } else {
                    AddEditSuggestionState(
                        uid = uid,
                        name = TextFieldValue(
                            text = support.name,
                            selection = TextRange(support.name.length),
                            composition = TextRange(support.name.length)
                        ),
                        isNameError = false
                    )
                }
                _addEditSuggestionState.tryEmit(state)
            }
        }
    }
}